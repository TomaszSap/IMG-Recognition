package com.tsapiszczak.imgrecognition

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.tsapiszczak.imgrecognition.databinding.ActivityPhotoRecognitionBinding
import com.tsapiszczak.imgrecognition.machinelearning.ConvolutionalNeuralNetworkConfig

class PhotoRecognitionActivity : AppCompatActivity() {
    val imageRecognition: ConvolutionalNeuralNetworkConfig = ConvolutionalNeuralNetworkConfig()
    private lateinit var binding:ActivityPhotoRecognitionBinding
    private lateinit var imageView: ImageView
    var isPhoto:Boolean= false
    override fun onCreate(savedInstanceState: Bundle?) {
        imageRecognition.init(false)
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.getPhoto.setOnClickListener {
            getContext.launch("image/*")

        }
        binding.backButton.setOnClickListener{
            backButtonClick()
        }
        binding.detectFromPhoto.setOnClickListener{
            detect()
        }
    }
    var getContext = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()
    ) { result ->
        if (result != null) {
            isPhoto=true
            binding.storageImage!!.setImageURI(result)

        }
    }

    private fun backButtonClick()
    {
        finish()
    }
    private fun detect()
    {
        if(isPhoto==true){
        imageRecognition.detectFromPhoto(this,binding)
            isPhoto=false
        }
        else
            Toast.makeText(this,"Photo not selected",Toast.LENGTH_LONG).show()

    }
    }