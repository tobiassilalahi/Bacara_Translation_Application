package com.wicarateam.bacara.ui.camera

import android.graphics.*
import android.graphics.Bitmap.Config
import android.graphics.Paint.Style
import android.media.ImageReader.OnImageAvailableListener
import android.os.Process
import android.os.SystemClock
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.wicarateam.bacara.R
import com.wicarateam.bacara.helper.Constant.DESIRED_PREVIEW_SIZE
import com.wicarateam.bacara.helper.Constant.MAINTAIN_ASPECT
import com.wicarateam.bacara.helper.Constant.MINIMUM_CONFIDENCE_TF_OD_API
import com.wicarateam.bacara.helper.Constant.MODE
import com.wicarateam.bacara.helper.Constant.SAVE_PREVIEW_BITMAP
import com.wicarateam.bacara.helper.Constant.TEXT_SIZE_DIP_10
import com.wicarateam.bacara.helper.Constant.TF_OD_API_INPUT_SIZE
import com.wicarateam.bacara.helper.Constant.TF_OD_API_IS_QUANTIZED
import com.wicarateam.bacara.helper.Constant.TF_OD_API_LABELS_FILE
import com.wicarateam.bacara.helper.Constant.TF_OD_API_MODEL_FILE
import com.wicarateam.bacara.helper.Constant.WAIT_TIME
import com.wicarateam.bacara.ui.camera.customview.OverlayView
import com.wicarateam.bacara.ui.camera.helper.BorderedText
import com.wicarateam.bacara.ui.camera.helper.ImageUtils
import com.wicarateam.bacara.ui.camera.tflite.Classifier
import com.wicarateam.bacara.ui.camera.tflite.TFLiteObjectDetectionAPIModel
import com.wicarateam.bacara.ui.camera.tracking.MultiBoxTracker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.lang.Thread.interrupted
import java.util.*
import kotlin.system.exitProcess

class DetectorActivity : CameraActivity(), OnImageAvailableListener {

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {}

    override fun onClick(p0: View?) {}

    private lateinit var trackingOverlay: OverlayView
    private var sensorOrientation: Int? = null

    private var detector: Classifier? = null

    private var lastProcessingTimeMs: Long = 0
    private var rgbFrameBitmap: Bitmap? = null
    private var croppedBitmap: Bitmap? = null
    private var cropCopyBitmap: Bitmap? = null

    private var computingDetection = false

    private var timestamp: Long = 0

    private var frameToCropTransform: Matrix? = null
    private var cropToFrameTransform: Matrix? = null

    private var tracker: MultiBoxTracker? = null

    private var borderedText: BorderedText? = null

    private var textList = mutableListOf<String>()

    override val layoutId: Int
        get() = R.layout.camera_connection_fragment_tracking

    override val desiredPreviewFrameSize: Size
        get() = DESIRED_PREVIEW_SIZE

    override fun onPreviewSizeChosen(size: Size, cameraRotation: Int) {
        val textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP_10, resources.displayMetrics
        )
        borderedText = BorderedText(textSizePx)
        borderedText?.setTypeface(Typeface.MONOSPACE)

        tracker = MultiBoxTracker(this)

        var cropSize = TF_OD_API_INPUT_SIZE

        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                assets,
                TF_OD_API_MODEL_FILE,
                TF_OD_API_LABELS_FILE,
                TF_OD_API_INPUT_SIZE,
                TF_OD_API_IS_QUANTIZED
            )
            cropSize = TF_OD_API_INPUT_SIZE
        } catch (e: IOException) {
            e.printStackTrace()
            Timber.e(e, "Exception initializing classifier!")
            val toast = Toast.makeText(
                applicationContext, "Classifier could not be initialized", Toast.LENGTH_SHORT
            )
            toast.show()
            finish()
        }
        previewWidth = size.width
        previewHeight = size.height

        sensorOrientation = cameraRotation - screenOrientation
        Timber.i("Initializing at size $previewWidth $previewHeight")
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888)
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888)

        frameToCropTransform = ImageUtils.getTransformationMatrix(
            previewWidth, previewHeight,
            cropSize, cropSize,
            sensorOrientation!!, MAINTAIN_ASPECT
        )

        cropToFrameTransform = Matrix()
        frameToCropTransform?.invert(cropToFrameTransform)

        trackingOverlay = findViewById<View>(R.id.tracking_overlay) as OverlayView
        trackingOverlay.addCallback(
            object : OverlayView.DrawCallback {
                override fun drawCallback(canvas: Canvas) {
                    tracker?.draw(canvas)
                    if (isDebug) {
                        tracker?.drawDebug(canvas)
                    }
                }
            })

        tracker?.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation!!)
    }

    override fun processImage() {
        ++timestamp
        val currTimestamp = timestamp
        trackingOverlay.postInvalidate()

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage()
            return
        }
        computingDetection = true
        Timber.i("Preparing image $currTimestamp for detection in bg thread.")

        rgbFrameBitmap?.setPixels(
            getRgbBytes(),
            0,
            previewWidth,
            0,
            0,
            previewWidth,
            previewHeight
        )

        readyForNextImage()

        val canvas = Canvas(croppedBitmap!!)
        canvas.drawBitmap(rgbFrameBitmap!!, frameToCropTransform!!, null)
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap!!)
        }

        runInBackground {
            Timber.i("Running detection on image $currTimestamp")
            val startTime = SystemClock.uptimeMillis()
            val results = detector!!.recognizeImage(croppedBitmap!!)
            lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime

            cropCopyBitmap = Bitmap.createBitmap(croppedBitmap!!)
            val canvas = Canvas(cropCopyBitmap!!)
            val paint = Paint()
            paint.color = Color.RED
            paint.style = Style.STROKE
            paint.strokeWidth = 2.0f

            var minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API
            when (MODE) {
                DetectorMode.TF_OD_API -> minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API
            }

            val mappedRecognitions = LinkedList<Classifier.Recognition>()

            for (result in results) {
                val location = result.location
                if (result.confidence > minimumConfidence) {
                    canvas.drawRect(location, paint)

                    cropToFrameTransform?.mapRect(location)

                    result.location = location
                    mappedRecognitions.add(result)
                    result.title?.let { setDetectedTranslation(it) }
                    break
                }
            }

            tracker?.trackResults(mappedRecognitions, currTimestamp)
            trackingOverlay.postInvalidate()

            computingDetection = false

        }
    }

    private fun setDetectedTranslation(detected: String) {
        val translatedText = findViewById<TextView>(R.id.tv_translated)
        if (textList.contains(detected)) {
            if (textList[textList.size - 1] == detected) {
            } else {
                textList.remove(detected)
                textList.add(detected)
            }
        } else {
            textList.add(detected)
        }

        lifecycleScope.launch(Dispatchers.Default) {
            while (!interrupted()) {
                delay(1000)
                withContext(Dispatchers.Main) {
                    translatedText.text = textList[textList.size - 1]
                    setOnSpeak()
                }
            }
        }
    }

    private fun setOnSpeak() {
        val onSpeakButton = findViewById<ImageView>(R.id.iv_speak)
        val translatedText = findViewById<TextView>(R.id.tv_translated)
        onSpeakButton.setOnClickListener {
            onSpeak(translatedText.text.toString())
            onSpeakButton.setImageResource(R.drawable.ic_baseline_volume_up_24)
        }
    }

    enum class DetectorMode {
        TF_OD_API
    }

    override fun setNumThreads(numThreads: Int) {
        runInBackground { detector?.setNumThreads(numThreads) }
    }

    override fun onDestroy() {
        mMainViewModel?.dispose()
        super.onDestroy()
    }

    private fun onSpeak(translationText: String) {
        mMainViewModel?.speak(translationText)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSubscribe { initTTSVoice() }
            ?.subscribe(object : CompletableObserver {
                override fun onSubscribe(d: @NonNull Disposable?) {}
                override fun onComplete() {
                    makeToast("Play Translation Voice", false)
                }

                override fun onError(e: @NonNull Throwable?) {
                    makeToast("Translation Voice Failed", false)
//                    makeToast("Translation Voice Failed " + e?.message, true)
                }
            })
    }

    override fun onBackPressed() {
        exitApp()
    }

    private fun makeToast(text: String, longShow: Boolean) {
        Toast.makeText(this, text, if (longShow) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    private fun exitApp() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            // exit app
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } else {
            TOUCH_TIME = System.currentTimeMillis()
            makeToast("Press back again to Exit", false)
        }
    }

    private fun initTTSVoice() {
        val languageCode = "id-ID"
        val voiceName = "id-ID-Standard-A"
        val pitch = 0.0f
        val speakRate = 1.0f
        mMainViewModel?.initTTSVoice(languageCode, voiceName, pitch, speakRate)
    }
}