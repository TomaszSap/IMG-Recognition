package com.tsapiszczak.imgrecognition
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityCameraxBinding
import com.tsapiszczak.imgrecognition.machinelearning.*
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class CameraxActivity : AppCompatActivity()
{
    private val backgroundExecutor: ExecutorService by lazy { Executors.newSingleThreadExecutor() }
    private lateinit var cameraProcessor: CameraProcessor
    private lateinit var binding: ActivityCameraxBinding
    private var imageCapture: ImageCapture?=null
    private lateinit var photoDirectory:File
    //val imageRecognition: ConvolutionalNeuralNetworkConfig = ConvolutionalNeuralNetworkConfig()
    private lateinit var cameraProviderFuture:ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // imageRecognition.init(true)
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
                val message="The photo has been saved"
                Toast.makeText(this@CameraxActivity,"The photo has been saved",Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(Constants.cam," on Exception: ${exception.message}",exception)
            }
        })
    }
    private fun startCamera()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val imageAnalyzer = ImageAnalyzer(
                applicationContext,::updateUI
            )
            runOnUiThread {
                cameraProcessor = CameraProcessor(
                imageAnalyzer,
                cameraProviderFuture.get(),
                binding.cameraObj.surfaceProvider,
               backgroundExecutor
            )
                if (!cameraProcessor.bindCameraUseCases(this)) {
                    Log.v("Could not initialize camera.","")
                }
            }
        },backgroundExecutor)
    }
    private fun updateUI(result: AnalysisResult?) {
        runOnUiThread {
           // clearUi()
            if (result == null) {
                binding.detectObjectView.setDetection(null)
                return@runOnUiThread
            }

            if (result is AnalysisResult.WithPrediction) {
               binding.detectObjectView.setDetection(result)
              //  detected_item_text.text = result.prediction.getText(this)
            //    val confidencePercent = result.prediction.confidence * 100
               // percentMeter.progress = confidencePercent.toInt()
               // detected_item_confidence.text = "%.2f%%".format(confidencePercent)
            } else {
                binding.detectObjectView.setDetection(null)
            }
           // inference_time_value.text = getString(androidx.camera.core.R.string.inference_time_placeholder, result.processTimeMs)
        }
    }
    private fun clearUi() {
      //  detected_item_text.text = ""
     //   detected_item_confidence.text = ""
       // inference_time_value.text = ""
       // percentMeter.progress = 0
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
    try{
        cameraProvider.unbindAll()
    val preview=Preview.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
        .also {
            it.setSurfaceProvider(binding.cameraObj.surfaceProvider)
        }
    val cameraSelector=CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    preview.setSurfaceProvider(binding.cameraObj.surfaceProvider)
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageAnalysis)
    }catch (e:RuntimeException)
    {
        Log.e("Detect error ","failed detect objects",e)
    }
}
}
