package com.tsapiszczak.imgrecognition.machinelearning

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class ImageAnalyzerProxy(private val imageAnalyzer: ModelConfig): ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        imageAnalyzer.analyze(image,false)
    }
}