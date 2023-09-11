package com.wicarateam.bacara.ui.camera.helper

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.os.Bundle
import android.os.HandlerThread
import android.util.Size
import android.view.*
import androidx.fragment.app.Fragment
import com.wicarateam.bacara.R
import com.wicarateam.bacara.helper.Constant.ORIENTATIONS
import com.wicarateam.bacara.ui.camera.customview.AutoFitTextureView
import timber.log.Timber
import java.io.IOException

open class LegacyCameraConnectionFragment(
    private val imageListener: Camera.PreviewCallback,
    private val layout: Int, private val desiredSize: Size
) : Fragment() {
    private var camera: Camera? = null
    private var textureView: AutoFitTextureView? = null
    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(
            texture: SurfaceTexture, width: Int, height: Int
        ) {

            val index = cameraId
            camera = Camera.open(index)

            try {
                val parameters = camera?.parameters
                val focusModes = parameters?.supportedFocusModes
                if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
                }
                val cameraSizes = parameters?.supportedPreviewSizes
                val sizes = cameraSizes?.let { arrayOfNulls<Size>(it.size) }
                var i = 0
                if (cameraSizes != null) {
                    for (size in cameraSizes) {
                        sizes?.set(i++, Size(size.width, size.height))
                    }
                }
                val previewSize = sizes?.let {
                    CameraConnectionFragment.chooseOptimalSize(
                        it, desiredSize.width, desiredSize.height
                    )
                }
                previewSize?.width?.let { parameters.setPreviewSize(it, previewSize.height) }
                camera?.setDisplayOrientation(90)
                camera?.parameters = parameters
                camera?.setPreviewTexture(texture)
            } catch (exception: IOException) {
                camera?.release()
            }

            camera?.setPreviewCallbackWithBuffer(imageListener)
            val s = camera?.parameters?.previewSize
            if (s != null) {
                camera?.addCallbackBuffer(ByteArray(ImageUtils.getYUVByteSize(s.height, s.width)))
            }

            if (s != null) {
                textureView?.setAspectRatio(s.height, s.width)
            }

            camera?.startPreview()
        }

        override fun onSurfaceTextureSizeChanged(
            texture: SurfaceTexture,
            width: Int,
            height: Int
        ) {
        }

        override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
            return true
        }

        override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
    }

    private var backgroundThread: HandlerThread? = null
    private val cameraId: Int
        get() {
            val ci = CameraInfo()
            for (i in 0 until Camera.getNumberOfCameras()) {
                Camera.getCameraInfo(i, ci)
                if (ci.facing == CameraInfo.CAMERA_FACING_BACK) return i
            }
            return -1
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textureView = view.findViewById<View>(R.id.texture) as AutoFitTextureView
    }

    override fun onResume() {
        super.onResume()
        startBackgroundThread()

        if (textureView!!.isAvailable) {
            camera?.startPreview()
        } else {
            textureView?.surfaceTextureListener = surfaceTextureListener
        }
    }

    override fun onPause() {
        stopCamera()
        stopBackgroundThread()
        super.onPause()
    }

    private fun startBackgroundThread() {
        backgroundThread = HandlerThread("CameraBackground")
        backgroundThread?.start()
    }

    private fun stopBackgroundThread() {
        backgroundThread?.quitSafely()
        try {
            backgroundThread?.join()
            backgroundThread = null
        } catch (e: InterruptedException) {
            Timber.e("Exception!: $e")
        }

    }

    private fun stopCamera() {
        if (camera != null) {
            camera?.stopPreview()
            camera?.setPreviewCallback(null)
            camera?.release()
            camera = null
        }
    }

    companion object {
        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }
}
