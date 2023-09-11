package com.wicarateam.bacara.ui.camera.tracking

import android.content.Context
import android.graphics.*
import android.graphics.Paint.*
import android.text.TextUtils
import android.util.Pair
import android.util.TypedValue
import com.wicarateam.bacara.helper.Constant.COLORS
import com.wicarateam.bacara.helper.Constant.MIN_SIZE
import com.wicarateam.bacara.helper.Constant.TEXT_SIZE_DIP_18
import com.wicarateam.bacara.ui.camera.helper.BorderedText
import com.wicarateam.bacara.ui.camera.helper.ImageUtils
import com.wicarateam.bacara.ui.camera.tflite.Classifier.Recognition
import timber.log.Timber
import java.util.*

class MultiBoxTracker(context: Context) {
    private val screenRect: MutableList<Pair<Float, RectF>> = LinkedList()
    private val availableColors = LinkedList<Int>()
    private val trackedObjects = LinkedList<TrackedRecognition>()
    private val boxPaint = Paint()
    private val textSizePx: Float
    private val borderedText: BorderedText
    private var frameToCanvasMatrix: Matrix? = null
    private var frameWidth: Int = 0
    private var frameHeight: Int = 0
    private var sensorOrientation: Int = 0

    init {
        for (color in COLORS) {
            availableColors.add(color)
        }

        boxPaint.color = Color.RED
        boxPaint.style = Style.STROKE
        boxPaint.strokeWidth = 10.0f
        boxPaint.strokeCap = Cap.ROUND
        boxPaint.strokeJoin = Join.ROUND
        boxPaint.strokeMiter = 100f

        textSizePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP_18, context.resources.displayMetrics
        )
        borderedText = BorderedText(textSizePx)
    }

    @Synchronized
    fun setFrameConfiguration(
        width: Int, height: Int, sensorOrientation: Int
    ) {
        frameWidth = width
        frameHeight = height
        this.sensorOrientation = sensorOrientation
    }

    @Synchronized
    fun drawDebug(canvas: Canvas) {
        val textPaint = Paint()
        textPaint.color = Color.WHITE
        textPaint.textSize = 60.0f

        val boxPaint = Paint()
        boxPaint.color = Color.RED
        boxPaint.alpha = 200
        boxPaint.style = Style.STROKE

        for (detection in screenRect) {
            val rect = detection.second
            canvas.drawRect(rect, boxPaint)
            canvas.drawText("" + detection.first, rect.left, rect.top, textPaint)
            borderedText.drawText(canvas, rect.centerX(), rect.centerY(), "" + detection.first)
        }
    }

    @Synchronized
    fun trackResults(results: List<Recognition>, timestamp: Long) {
        Timber.i("Processing ${results.size} results from $timestamp")
        processResults(results)
    }

    @Synchronized
    fun draw(canvas: Canvas) {
        val rotated = sensorOrientation % 180 == 90
        val multiplier =
            (canvas.height / (if (rotated) frameWidth else frameHeight).toFloat()).coerceAtMost(
                canvas.width / (if (rotated) frameHeight else frameWidth).toFloat()
            )
        frameToCanvasMatrix = ImageUtils.getTransformationMatrix(
            frameWidth,
            frameHeight,
            (multiplier * if (rotated) frameHeight else frameWidth).toInt(),
            (multiplier * if (rotated) frameWidth else frameHeight).toInt(),
            sensorOrientation,
            false
        )
        for (recognition in trackedObjects) {
            val trackedPos = RectF(recognition.location)

            frameToCanvasMatrix?.mapRect(trackedPos)
            boxPaint.color = recognition.color

            val cornerSize = trackedPos.width().coerceAtMost(trackedPos.height()) / 8.0f
            canvas.drawRoundRect(trackedPos, cornerSize, cornerSize, boxPaint)

            val labelString = if (!TextUtils.isEmpty(recognition.title))
                String.format("%s %.2f", recognition.title, 100 * recognition.detectionConfidence)
            else
                String.format("%.2f", 100 * recognition.detectionConfidence)
            borderedText.drawText(
                canvas, trackedPos.left + cornerSize, trackedPos.top, "$labelString%", boxPaint
            )
        }
    }

    private fun processResults(results: List<Recognition>) {
        val rectToTrack = LinkedList<Pair<Float, Recognition>>()

        screenRect.clear()
        val rgbFrameToScreen = Matrix(frameToCanvasMatrix)

        for (result in results) {
            val detectionFrameRect = RectF(result.location)
            val detectionScreenRect = RectF()
            rgbFrameToScreen.mapRect(detectionScreenRect, detectionFrameRect)
            Timber.v("Result! Frame: ${result.location} mapped to screen $detectionScreenRect")
            screenRect.add(Pair(result.confidence, detectionScreenRect))
            if (detectionFrameRect.width() < MIN_SIZE || detectionFrameRect.height() < MIN_SIZE) {
                Timber.w("Degenerate rectangle! $detectionFrameRect")
                continue
            }
            rectToTrack.add(Pair(result.confidence, result))
        }

        if (rectToTrack.isEmpty()) {
            Timber.v("Nothing to track, aborting.")
            return
        }

        trackedObjects.clear()
        for (potential in rectToTrack) {
            val trackedRecognition = TrackedRecognition()
            trackedRecognition.detectionConfidence = potential.first
            trackedRecognition.location = RectF(potential.second.location)
            trackedRecognition.title = potential.second.title
            trackedRecognition.color = COLORS[trackedObjects.size]
            trackedObjects.add(trackedRecognition)

            if (trackedObjects.size >= COLORS.size) {
                break
            }
        }
    }

    private class TrackedRecognition {
        var location: RectF? = null
        var detectionConfidence: Float = 0.toFloat()
        var color: Int = 0
        var title: String? = null
    }
}
