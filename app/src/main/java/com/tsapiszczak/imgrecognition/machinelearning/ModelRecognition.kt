package com.tsapiszczak.imgrecognition.machinelearning

import androidx.camera.core.ImageProxy
import org.jetbrains.kotlinx.dl.onnx.inference.executionproviders.ExecutionProvider
import org.jetbrains.kotlinx.dl.onnx.inference.inferUsing
import org.jetbrains.kotlinx.dl.onnx.inference.objectdetection.SSDLikeModel
import org.jetbrains.kotlinx.dl.onnx.inference.objectdetection.detectObjects

class ModelRecognition(private val model: SSDLikeModel){
    fun analyze(image: ImageProxy, objectConficence: Float): ObjectFrameData? {
        val detections = model.inferUsing(ExecutionProvider.CPU()) {
            it.detectObjects(image, -1)
        }.filter{ it.probability >= objectConficence}
        if (detections.isEmpty()) return null
        return PredictedObject(detections)
    }
}