package com.tsapiszczak.imgrecognition

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.ImageReader
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding
import com.tsapiszczak.imgrecognition.machinelearning.ConvolutionalNeuralNetworkConfig



class CameraxActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityCameraxBinding
    private var imageReader: ImageReader?=null
    val imageRecognition: ConvolutionalNeuralNetworkConfig = ConvolutionalNeuralNetworkConfig()
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageRecognition.init()
        binding = ActivityCameraxBinding.inflate(layoutInflater)
        setContentView(binding.root)

      //  startCamera()
        //new
        cameraProviderFuture=ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider=cameraProviderFuture.get()
            bindPreview(cameraProvider)
        },ContextCompat.getMainExecutor(this))
        //end of new
        binding.backButton.setOnClickListener{
            backButtonClick()
        }
    }
    private fun backButtonClick()
    {
        val intent = Intent(this@CameraxActivity, MainActivity::class.java)
        startActivity(intent)
    }
    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera()
    {
        val processCameraProvider= ProcessCameraProvider.getInstance(this)

        processCameraProvider.addListener({
            val cameraProvider:ProcessCameraProvider=processCameraProvider.get()
            val preview = Preview.Builder()
                .build().also { mPreview ->
                mPreview.setSurfaceProvider(binding.cameraObj.surfaceProvider)
            }
            try
            {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this ,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview)
                imageRecognition.detect(this,binding)

            }
            catch(e: Exception)
            {
                Log.e(Constants.cam,"STARTING OF CAMERA FAILED BY:",e)
            }
        }, ContextCompat.getMainExecutor(this))

    }

private fun bindPreview(cameraProvider: ProcessCameraProvider)
{
    val preview=Preview.Builder().build()
    val cameraSelector=CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(binding.cameraObj.surfaceProvider)
    imageRecognition.detect(this,binding)
    cameraProvider.bindToLifecycle(this,cameraSelector,imageRecognition.imageAnalysis,preview)
}

}