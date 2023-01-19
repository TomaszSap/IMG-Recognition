package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels

class ModelConfig(
    context: Context,
    private val updateView: (GetObject?) -> Unit
) {
    private val hub = ONNXModelHub(context)
    val modelResource= ONNXModels.ObjectDetection.SSDMobileNetV1.pretrainedModel(hub)
    fun analyze(image: ImageProxy) {
        val model=ModelRecognition(modelResource)
        val result= model.analyze(image, 0.5f)
        image.close()
        if (result != null && result.confidence>0.5f) {
            updateView(
                GetObject(
                    result,
                    image.width,
                    image.height
                )
            )
        }
    }
}
