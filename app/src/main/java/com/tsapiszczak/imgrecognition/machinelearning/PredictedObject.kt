package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import com.tsapiszczak.imgrecognition.R
import org.jetbrains.kotlinx.dl.api.inference.FlatShape
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject

class PredictedObject(private val detections: List<DetectedObject>): ObjectFrameData {
    override val shapes: List<FlatShape<*>> get() = detections
    override val confidence: Float get() = detections.first().probability
    override fun getText(context: Context): String {
      val singleObject = detections.singleOrNull()
       if (singleObject != null) return singleObject.label ?: "Not recognized"
       return context.getString(R.string.label_objects, detections.size)
    }
}