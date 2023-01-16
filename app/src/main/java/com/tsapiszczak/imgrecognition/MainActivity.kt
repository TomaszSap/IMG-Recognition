package com.tsapiszczak.imgrecognition

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tsapiszczak.imgrecognition.constants.Constants
import com.tsapiszczak.imgrecognition.databinding.ActivityMainBinding



class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding //connecting xml with class, no need to use findviewbyid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.startButton.setOnClickListener {startButtonClick()
        }
    }
    private fun startButtonClick() {

        if (permissionGranted()&&permissionGrantedd() ){
        val intent = Intent(this@MainActivity, CameraxActivity::class.java)
        startActivity(intent)}
        else{
            Toast.makeText(this,"You have denied permissions",Toast.LENGTH_LONG).show()
        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS,
            Constants.REQUEST_CODE_PERMISSIONS)
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONSS,
                Constants.REQUEST_CODE_PERMISSIONS)
        }
    }
    private fun permissionGranted()=
        Constants.PERMISSIONS.all{
            ContextCompat.checkSelfPermission(baseContext, it)==
                    PackageManager.PERMISSION_GRANTED
        }

    private fun permissionGrantedd()=Constants.PERMISSIONSS.all{
        ContextCompat.checkSelfPermission(baseContext, it)==
                PackageManager.PERMISSION_GRANTED
    }

}