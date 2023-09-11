package com.wicarateam.bacara.helper

import android.Manifest
import android.graphics.Color
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import com.wicarateam.bacara.ui.camera.DetectorActivity

object Constant {
    const val ARG_MESSAGE = "message"
    val ORIENTATIONS = SparseIntArray()
    const val DEFAULT_TAG = "tensorflow"
    const val DEFAULT_MIN_LOG_LEVEL = Log.DEBUG
    const val TEXT_SIZE_DIP_18 = 18f
    const val MIN_SIZE = 16.0f
    val COLORS = intArrayOf(
        Color.BLUE, Color.RED, Color.GREEN,
        Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE, Color.parseColor("#55FF55"),
        Color.parseColor("#FFA500"), Color.parseColor("#FF8888"),
        Color.parseColor("#AAAAFF"), Color.parseColor("#FFFFAA"),
        Color.parseColor("#55AAAA"), Color.parseColor("#AA33AA"),
        Color.parseColor("#0D0068")
    )
    const val PERMISSIONS_REQUEST = 1
    const val PERMISSION_CAMERA = Manifest.permission.CAMERA
    const val PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    const val TF_OD_API_INPUT_SIZE = 320
    const val TF_OD_API_IS_QUANTIZED = false
    const val TF_OD_API_MODEL_FILE = "model.tflite"
    const val TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt"
    const val MINIMUM_CONFIDENCE_TF_OD_API = 0.51f
    const val MAINTAIN_ASPECT = false
    val DESIRED_PREVIEW_SIZE = Size(640, 480)
    const val SAVE_PREVIEW_BITMAP = false
    const val TEXT_SIZE_DIP_10 = 10f
    const val NUM_DETECTIONS = 10
    const val IMAGE_MEAN = 128.0f
    const val IMAGE_STD = 128.0f
    val MODE = DetectorActivity.DetectorMode.TF_OD_API
    const val WAIT_TIME = 2000L
    const val MEETING_PREF = "USER_PREF"
}