package com.wicarateam.bacara.ui.camera.tflite

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Trace
import android.util.ArrayMap
import com.wicarateam.bacara.helper.Constant.IMAGE_MEAN
import com.wicarateam.bacara.helper.Constant.IMAGE_STD
import com.wicarateam.bacara.helper.Constant.NUM_DETECTIONS
import org.tensorflow.lite.Interpreter
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.*

class TFLiteObjectDetectionAPIModel private constructor() : Classifier {
    override val statString: String
        get() = TODO("not implemented")
    private var isModelQuantized: Boolean = false
    private var inputSize: Int = 0
    private val labels = Vector<String>()
    private var intValues: IntArray? = null
    private var outputLocations: Array<Array<FloatArray>>? = null
    private var outputClasses: Array<FloatArray>? = null
    private var outputScores: Array<FloatArray>? = null
    private var numDetections: FloatArray? = null
    private var imgData: ByteBuffer? = null
    private var tfLite: Interpreter? = null
    override fun recognizeImage(bitmap: Bitmap): List<Classifier.Recognition> {
        Trace.beginSection("recognizeImage")
        Trace.beginSection("preprocessBitmap")
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        imgData?.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues?.get(i * inputSize + j)
                if (isModelQuantized) {
                    // Quantized model
                    if (pixelValue != null) {
                        imgData?.put((pixelValue shr 16 and 0xFF).toByte())
                        imgData?.put((pixelValue shr 8 and 0xFF).toByte())
                        imgData?.put((pixelValue and 0xFF).toByte())
                    }
                } else { // Float model
                    if (pixelValue != null) {
                        imgData?.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                        imgData?.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                        imgData?.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    }
                }
            }
        }
        Trace.endSection() // preprocessBitmap

        // Copy the input data into TensorFlow.
        Trace.beginSection("feed")
        outputLocations = Array(1) { Array(NUM_DETECTIONS) { FloatArray(4) } }
        outputClasses = Array(1) { FloatArray(NUM_DETECTIONS) }
        outputScores = Array(1) { FloatArray(NUM_DETECTIONS) }
        numDetections = FloatArray(1)

        val inputArray = arrayOf<Any>(imgData!!)
        val outputMap = ArrayMap<Int, Any>()
        outputMap[0] = outputLocations
        outputMap[1] = outputClasses
        outputMap[2] = outputScores
        outputMap[3] = numDetections
        Trace.endSection()

        // Run the inference call.
        Trace.beginSection("run")
        tfLite?.runForMultipleInputsOutputs(inputArray, outputMap)
        Trace.endSection()

        // Show the best detections.
        // after scaling them back to the input size.
        val recognitions = ArrayList<Classifier.Recognition>(NUM_DETECTIONS)
        for (i in 0 until NUM_DETECTIONS) {
            val detection = RectF(
                outputLocations!![0][i][1] * inputSize,
                outputLocations!![0][i][0] * inputSize,
                outputLocations!![0][i][3] * inputSize,
                outputLocations!![0][i][2] * inputSize
            )

            val labelOffset = 1
            recognitions.add(
                Classifier.Recognition(
                    "" + i,
                    labels[outputClasses!![0][i].toInt() + labelOffset],
                    outputScores!![0][i],
                    detection
                )
            )
        }
        Trace.endSection() // "recognizeImage"
        return recognitions
    }

    override fun enableStatLogging(debug: Boolean) {}

    override fun close() {}

    override fun setNumThreads(numThreads: Int) {
        if (tfLite != null) tfLite?.setNumThreads(numThreads)
    }

    companion object {
        @Throws(IOException::class)
        private fun loadModelFile(assets: AssetManager, modelFilename: String): MappedByteBuffer {
            val fileDescriptor = assets.openFd(modelFilename)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        }

        @Throws(IOException::class)
        fun create(
            assetManager: AssetManager,
            modelFilename: String,
            labelFilename: String,
            inputSize: Int,
            isQuantized: Boolean
        ): Classifier {
            val d = TFLiteObjectDetectionAPIModel()

            var labelsInput: InputStream? = null
            val actualFilename = labelFilename.split("file:///android_asset/".toRegex())
                .dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            labelsInput = assetManager.open(actualFilename)
            val br: BufferedReader?
            br = BufferedReader(InputStreamReader(labelsInput))
            while (br.readLine()?.let { d.labels.add(it) } != null);
            br.close()

            d.inputSize = inputSize

            try {
                val options = Interpreter.Options()
                options.setNumThreads(4)
                options.setUseNNAPI(false)
                d.tfLite = Interpreter(loadModelFile(assetManager, modelFilename), options)
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            d.isModelQuantized = isQuantized
            // Pre-allocate buffers.
            val numBytesPerChannel: Int = if (isQuantized) {
                1 // Quantized
            } else {
                4 // Floating point
            }
            d.imgData =
                ByteBuffer.allocateDirect(1 * d.inputSize * d.inputSize * 3 * numBytesPerChannel)
            d.imgData?.order(ByteOrder.nativeOrder())
            d.intValues = IntArray(d.inputSize * d.inputSize)
            d.outputLocations = Array(1) { Array(NUM_DETECTIONS) { FloatArray(4) } }
            d.outputClasses = Array(1) { FloatArray(NUM_DETECTIONS) }
            d.outputScores = Array(1) { FloatArray(NUM_DETECTIONS) }
            d.numDetections = FloatArray(1)
            return d
        }
    }
}
