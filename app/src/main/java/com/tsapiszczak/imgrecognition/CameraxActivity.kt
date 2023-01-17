package com.tsapiszczak.imgrecognition

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.ImageReader
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding
import com.tsapiszczak.imgrecognition.machinelearning.ConvolutionalNeuralNetworkConfig
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class CameraxActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityCameraxBinding
    private var imageCapture: ImageCapture?=null
    private lateinit var photoDirectory:File
    val imageRecognition: ConvolutionalNeuralNetworkConfig = ConvolutionalNeuralNetworkConfig()
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageRecognition.init(true)
        binding = ActivityCameraxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        photoDirectory= setFileDirectory()
        binding.backButton.setOnClickListener{
            backButtonClick()
        }
        binding.takePhoto.setOnClickListener{
            takePhoto()
        }
        cameraProviderFuture=ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()
            detect(cameraProvider)
        },ContextCompat.getMainExecutor(this))
    }
    private fun backButtonClick()
    {
        finish()
    }
    private fun takePhoto()
    {
   val imageCapture=imageCapture?:return
        val photo=File(photoDirectory,
            SimpleDateFormat("yy-MM-dd-HH-mm-ss-SSS", Locale.getDefault()).format(System.currentTimeMillis()) +".jpg")
        val outputFileOption=ImageCapture.OutputFileOptions.Builder(photo).build()
        imageCapture.takePicture(outputFileOption,ContextCompat.getMainExecutor(this),object:ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
               val savedUri= Uri.fromFile(photo)
                val message="The photo has been saved"
                Toast.makeText(this@CameraxActivity,"The photo has been saved",Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(Constants.cam," on Exception: ${exception.message}",exception)
            }
        })
    }
private fun setFileDirectory():File
{
    val setDirectory=externalMediaDirs.firstOrNull()?.let {file ->
        File(file,resources.getString(R.string.app_name)).apply {
            mkdirs()
        }
    }
       return if(setDirectory!=null && setDirectory.exists())
           setDirectory else filesDir
    }

private fun detect(cameraProvider: ProcessCameraProvider)
{
    val preview=Preview.Builder().build()
    imageCapture=ImageCapture.Builder().build()
    val cameraSelector=CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(binding.cameraObj.surfaceProvider)
    imageRecognition.detect(this,binding)
    cameraProvider.bindToLifecycle(this,cameraSelector,imageRecognition.imageAnalysis,preview,imageCapture)
}
}