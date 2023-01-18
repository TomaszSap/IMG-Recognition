package com.tsapiszczak.imgrecognition.machinelearning

import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.ExecutorService

class CameraProcessor(
    val imageAnalyzer: ImageAnalyzer,
    private val cameraProvider: ProcessCameraProvider,
    private val surfaceProvider: Preview.SurfaceProvider,
    private val executor: ExecutorService
) {
    private val cameraSelector get() = CameraSelector.DEFAULT_BACK_CAMERA

    fun bindCameraUseCases(lifecycleOwner: LifecycleOwner): Boolean {
        try {
            cameraProvider.unbindAll()
            val imagePreview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, ImageAnalyzerProxy(imageAnalyzer))
                }

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                imagePreview,
                imageAnalysis
            )
            return true
        } catch (e: RuntimeException) {
            Log.e("CameraProcessor from CameraxActivity: ", "Use case binding failed", e)

        }
        return false
    }
}