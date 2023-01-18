package com.tsapiszczak.imgrecognition
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding
import com.tsapiszczak.imgrecognition.machinelearning.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraxActivity : AppCompatActivity()
{
    private val backgroundExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private lateinit var binding: ActivityCameraxBinding
    private var imageCapture: ImageCapture?=null
    private lateinit var photoDirectory:File
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        photoDirectory= setFileDirectory()
        binding.backButton.setOnClickListener{
            backButtonClick()
        }
        binding.takePhoto.setOnClickListener{
            takePhoto()
        }
        startCamera()
        binding.detectObjectView.scaleType = binding.cameraObj.scaleType
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
                Toast.makeText(this@CameraxActivity,"The photo has been saved",Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(Constants.cam," on Exception: ${exception.message}",exception)
            }
        })
    }
    private fun startCamera()
    {
        imageCapture=ImageCapture.Builder().build()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val imageAnalyzer = ImageAnalyzer(
                applicationContext,::updateView
            )
            runOnUiThread {
                val cameraProvider=cameraProviderFuture.get()
                val cameraSelector= CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    val imagePreview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build()
                        .also {
                            it.setSurfaceProvider(binding.cameraObj.surfaceProvider)
                        }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(backgroundExecutor, ImageAnalyzerProxy(imageAnalyzer))
                        }

                    cameraProviderFuture.get().bindToLifecycle(
                        this,
                        cameraSelector,
                        imagePreview,
                        imageAnalysis,imageCapture
                    )
                } catch (e: RuntimeException) {
                    Log.e("CameraProcessor from CameraxActivity: ", "Use case binding failed", e)

                }
            }
        },backgroundExecutor)
    }
    private fun updateView(result: AnalysisResult?) {
        runOnUiThread {
            if (result == null) {
                binding.detectObjectView.setDetection(null)
                return@runOnUiThread
            }

            if (result is AnalysisResult.WithPrediction) {
                binding.detectObjectView.setDetection(result)
            } else {
                binding.detectObjectView.setDetection(null)
            }
        }
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
}
