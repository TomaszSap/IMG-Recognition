package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.camera.view.PreviewView
import com.tsapiszczak.imgrecognition.frames.FrameConfig
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.visualization.*


 class DetectedObjectsDraw(context: Context, attrs: AttributeSet) :
    DetectorViewBase<GetObject>(context, attrs){
    private var bounds: PreviewImageBounds? = null
    var scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER
    val frameConfig= FrameConfig()
    override fun onDetectionSet(detection: GetObject?) {
        bounds = detection?.let {
            getPreviewImageBounds(it.width, it.height,width, height, scaleType)
        }
    }
    override fun Canvas.drawDetection(detection: GetObject) {
        val currentBounds = bounds ?: bounds()
        for (s in detection.frameData.shapes) {
            when (s) {
              is DetectedObject-> drawObject(
                  s,
                   frameConfig.framePaint,frameConfig.textPaint,
                    currentBounds
                )
           }
        }
    }
}


