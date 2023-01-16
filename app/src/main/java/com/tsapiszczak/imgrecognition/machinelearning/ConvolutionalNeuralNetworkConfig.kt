package com.tsapiszczak.imgrecognition.machinelearning

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.tsapiszczak.imgrecognition.FrameConfig
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding

//configuration,building and detection based on ... model
 class ConvolutionalNeuralNetworkConfig
{
     lateinit var objectDetector:ObjectDetector
     val imageAnalysis=ImageAnalysis.Builder().setTargetResolution(Size(1280,720))
         .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    val localModel=LocalModel.Builder().setAssetFilePath("mobilenet_model.tflite").build()

    fun init()
    {
        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE).enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()
        objectDetector=ObjectDetection.getClient(customObjectDetectorOptions)
    }
    @SuppressLint("UnsafeOptInUsageError")
    fun detect(context: Context, binding: ActivityCameraxBinding)
    {
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),{ imageProxy->
            val rotation=imageProxy.imageInfo.rotationDegrees
            val detect=imageProxy.image
            if(detect!=null)
            {
                val process=InputImage.fromMediaImage(detect,rotation)
               objectDetector.process(process).addOnSuccessListener{objects->
                    for(i in objects)
                    {
                        if (binding.cameraxLayout.childCount>1)
                            binding.cameraxLayout.removeViewAt(1)
                        val recognized= FrameConfig(context,text=i.labels.firstOrNull()?.text ?: "Not recognized",frame = i.boundingBox)
                        binding.cameraxLayout.addView(recognized)
                    }
                    imageProxy.close()
                }.addOnFailureListener{
                    Log.v("CameraxActivity ","Exception: ${it.message}")
                    imageProxy.close()
                }
            }
        })
    }
}