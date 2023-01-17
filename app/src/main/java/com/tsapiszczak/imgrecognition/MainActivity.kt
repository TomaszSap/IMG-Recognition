package com.tsapiszczak.imgrecognition

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityMainBinding
import org.jetbrains.kotlinx.dl.dataset.embedded.fashionMnist


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startButton.setOnClickListener {startButtonClick()
        }
        binding.storageButton.setOnClickListener {
            storageButtonClick()
        }
    }

    private fun storageButtonClick() {
        if (storagePermission() ){
            val intent = Intent(this@MainActivity, PhotoRecognitionActivity::class.java)
            startActivity(intent)}
        else{
            Toast.makeText(this,"You have denied permissions",Toast.LENGTH_LONG).show()

            ActivityCompat.requestPermissions(this, Constants.EXTERNAL_STORAGE_PERMISSION,
                Constants.EXTERNAL_REQUEST_CODE_PERMISSIONS)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun startButtonClick() {

        if (permissionGranted() && writeStoragePermissionGranted() ){
        val intent = Intent(this@MainActivity, CameraxActivity::class.java)
        startActivity(intent)}
        else{
            Toast.makeText(this,"You have denied permissions",Toast.LENGTH_LONG).show()
            if(!permissionGranted())
            ActivityCompat.requestPermissions(this, Constants.CAMERA_PERMISSIONS,
                Constants.REQUEST_CODE_PERMISSIONS)
            else
            ActivityCompat.requestPermissions(this, Constants.WRITE_EXTERNAL_STORAGE_PERMISSION,
                Constants.EXTERNAL_WRITE_REQUEST_CODE_PERMISSIONS)
        }
    }
    private fun permissionGranted():Boolean{
        return Constants.CAMERA_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(baseContext, it)==
                    PackageManager.PERMISSION_GRANTED
        }
    }
    private fun writeStoragePermissionGranted():Boolean {
        return  Constants.WRITE_EXTERNAL_STORAGE_PERMISSION.all {
            ContextCompat.checkSelfPermission(baseContext, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }
    private fun storagePermission() :Boolean {
        return  Constants.EXTERNAL_STORAGE_PERMISSION.all{
        ContextCompat.checkSelfPermission(baseContext, it)==
                PackageManager.PERMISSION_GRANTED}}
}