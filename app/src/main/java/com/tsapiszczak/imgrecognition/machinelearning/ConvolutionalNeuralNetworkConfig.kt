package com.tsapiszczak.imgrecognition.machinelearning

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.ImageView
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import com.tsapiszczak.imgrecognition.FrameConfig
import com.tsapiszczak.imgrecognition.R
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding
import com.tsapiszczak.imgrecognition.databinding.ActivityPhotoRecognitionBinding


//configuration,building and detection based on ... model
 class ConvolutionalNeuralNetworkConfig
{
     lateinit var objectDetector:ObjectDetector
     var imageAnalysis=ImageAnalysis.Builder().setTargetResolution(Size(1280,720))
         .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()
    val localModel=LocalModel.Builder().setAssetFilePath("mobilenet_model.tflite").build()

    fun init(isStream:Boolean)
    {
        if (isStream){
        val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE).enableClassification()
            .setClassificationConfidenceThreshold(0.5f)
            .setMaxPerObjectLabelCount(3)
            .build()
            objectDetector=ObjectDetection.getClient(customObjectDetectorOptions)
        }
        else{
            val customObjectDetectorOptions = CustomObjectDetectorOptions.Builder(localModel)
                .setDetectorMode(CustomObjectDetectorOptions.SINGLE_IMAGE_MODE).enableClassification()
                .setClassificationConfidenceThreshold(0.5f)
                .setMaxPerObjectLabelCount(3)
                .build()
            objectDetector=ObjectDetection.getClient(customObjectDetectorOptions)
        }
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
                        if (binding.cameraxLayout.childCount>3)
                            binding.cameraxLayout.removeViewAt(3)
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
   /* @SuppressLint("UnsafeOptInUsageError")
    fun detectFromPhoto(context: Context, binding: ActivityPhotoRecognitionBinding)
    {
        if (binding.photoRecognitionLayout.childCount>4)
                binding.photoRecognitionLayout.removeViewAt(4)
        val drawable:BitmapDrawable = binding.storageImage.getDrawable() as BitmapDrawable
        binding.storageImage.buildDrawingCache()
        val bmap: Bitmap = drawable.bitmap
        binding.storageImage.rotation.toInt()
        val process=InputImage.fromBitmap(bmap,binding.storageImage.rotation.toInt())
        objectDetector.process(process).addOnSuccessListener{objects->
            for(i in objects)
            {
                val recognized= FrameConfig(context,text=i.labels.firstOrNull()?.text ?: "Not recognized",frame = i.boundingBox)
                binding.photoRecognitionLayout.addView(recognized)
            }
        }.addOnFailureListener{
            Log.v("CameraxActivity ","Exception: ${it.message}")
        }*/
   @SuppressLint("UnsafeOptInUsageError")
   fun detectFromPhoto(context: Context, binding: ActivityPhotoRecognitionBinding)
   {
       binding.chipGroup.removeAllViews()
       val drawable:BitmapDrawable = binding.storageImage.getDrawable() as BitmapDrawable
       binding.storageImage.buildDrawingCache()
       val bmap: Bitmap = drawable.bitmap
       binding.storageImage.rotation.toInt()
       val process=InputImage.fromBitmap(bmap,binding.storageImage.rotation.toInt())
       objectDetector.process(process).addOnSuccessListener{objects->
           for(i in objects)
           {
              val x= Chip(context)//,null, com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice)
               x.isCloseIconVisible=true
               x.isClickable = false
               x.setTextColor(Color.parseColor("#0088BE"))
               x.setText(i.labels.firstOrNull()?.text ?: "Not recognized")
               binding.chipGroup.addView(x)
           }
       }.addOnFailureListener{
           Log.v("CameraxActivity ","Exception: ${it.message}")
       }
}



    private fun convertImageViewToBitmap(v: ImageView): Bitmap? {
        return (v.getDrawable() as BitmapDrawable).bitmap
    }

}