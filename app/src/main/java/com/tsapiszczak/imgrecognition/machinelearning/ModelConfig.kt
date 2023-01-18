package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import android.os.SystemClock
import androidx.camera.core.ImageProxy
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels

class ModelConfig(
    context: Context,
    private val uiUpdateCallBack: (AnalysisResult?) -> Unit
) {
    private val hub = ONNXModelHub(context)
    val modelResource= ONNXModels.ObjectDetection.SSDMobileNetV1.pretrainedModel(hub)
    fun analyze(image: ImageProxy, isImageFlipped: Boolean) {
        val model=ModelRecognition(modelResource)
        val result= model.analyze(image, 0.5f)
        val rotationDegrees = image.imageInfo.rotationDegrees
        image.close()

        if (result != null && result.confidence>0.6f) {
            uiUpdateCallBack(
                AnalysisResult.WithPrediction(
                    result,
                    ImageMetadata(image.width, image.height, isImageFlipped, rotationDegrees)
                )
            )
        }
    }
}