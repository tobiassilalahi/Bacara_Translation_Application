package com.wicarateam.bacara.ui.camera.tflite

import android.graphics.Bitmap
import android.graphics.RectF

interface Classifier {

    val statString: String
    fun recognizeImage(bitmap: Bitmap): List<Recognition>

    fun enableStatLogging(debug: Boolean)

    fun close()

    fun setNumThreads(numThreads: Int)

    class Recognition(
        val id: String?,
        val title: String?,
        val confidence: Float,
        internal var location: RectF
    ) {

        override fun toString(): String {
            var resultString = ""
            if (id != null) {
                resultString += "[$id] "
            }

            if (title != null) {
                resultString += "$title "
            }

            resultString += String.format("(%.1f%%) ", confidence * 100.0f)

            resultString += "$location "

            return resultString.trim { it <= ' ' }
        }
    }
}
