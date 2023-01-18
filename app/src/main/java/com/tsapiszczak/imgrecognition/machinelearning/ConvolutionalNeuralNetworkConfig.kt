package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import com.tsapiszczak.imgrecognition.FrameConfig
import com.tsapiszczak.imgrecognition.R
import org.jetbrains.kotlinx.dl.api.inference.FlatShape
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.onnx.inference.executionproviders.ExecutionProvider
import org.jetbrains.kotlinx.dl.onnx.inference.inferUsing
import org.jetbrains.kotlinx.dl.onnx.inference.objectdetection.SSDLikeModel
import org.jetbrains.kotlinx.dl.onnx.inference.objectdetection.detectObjects
import org.jetbrains.kotlinx.dl.visualization.*


//configuration,building and detection based on ... model
 class ConvolutionalNeuralNetworkConfig(context: Context, attrs: AttributeSet) :
    DetectorViewBase<AnalysisResult.WithPrediction>(context, attrs){
    private var bounds: PreviewImageBounds? = null
    var scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
    val frameConfig=FrameConfig()
    override fun onDetectionSet(detection: AnalysisResult.WithPrediction?) {
        frameConfig.framePaint
        bounds = detection?.let {
            getPreviewImageBounds(it.metadata.width, it.metadata.height, width, height, scaleType)
        }
    }
    override fun Canvas.drawDetection(detection: AnalysisResult.WithPrediction) {
        val currentBounds = bounds ?: bounds()
        for (s in detection.prediction.shapes) {
            when (val shape = if (detection.metadata.isImageFlipped) s.flip() else s) {
              is DetectedObject-> drawObject(
                   shape,
                   frameConfig.framePaint,frameConfig.textPaint,
                    currentBounds
                )
           }
        }
    }
    private fun FlatShape<*>.flip(): FlatShape<*> {
        return map { x, y -> 1 - x to y }
    }
}
class PredictedObject(private val detections: List<DetectedObject>) :
    Prediction {
    override val shapes: List<FlatShape<*>> get() = detections
    override val confidence: Float get() = detections.first().probability
    override fun getText(context: Context): String {
        val singleObject = detections.singleOrNull()
        if (singleObject != null) return singleObject.label ?: ""
        return context.getString(R.string.label_objects, detections.size)
    }
}
sealed class AnalysisResult(){
    class WithPrediction(
        val prediction: Prediction,
        val metadata: ImageMetadata
    ) : AnalysisResult()
}
data class ImageMetadata(
    val width: Int,
    val height: Int,
    val isImageFlipped: Boolean
) {
    constructor(width: Int, height: Int, isImageFlipped: Boolean, rotationDegrees: Int)
            : this(
        if (areDimensionSwitched(rotationDegrees)) height else width,
        if (areDimensionSwitched(rotationDegrees)) width else height,
        isImageFlipped
    )

    companion object {
        private fun areDimensionSwitched(rotationDegrees: Int): Boolean {
            return rotationDegrees == 90 || rotationDegrees == 270
        }
    }
}
 class ModelRecognition(private val model: SSDLikeModel){
      fun analyze(image: ImageProxy, confidenceThreshold: Float): Prediction? {
         val detections = model.inferUsing(ExecutionProvider.CPU()) {
             it.detectObjects(image, -1)
         }.filter { it.probability >= confidenceThreshold }
         if (detections.isEmpty()) return null

         return PredictedObject(detections)
     }
    class PredictedClass(private val label: String, val confidence: Float)  {
        val shapes: List<FlatShape<*>> get() = emptyList()
         fun getText(context: Context): String = label
    }
}
class ImageAnalyzerProxy(private val imageAnalyzer: ImageAnalyzer): ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        imageAnalyzer.analyze(image,true)
    }
}