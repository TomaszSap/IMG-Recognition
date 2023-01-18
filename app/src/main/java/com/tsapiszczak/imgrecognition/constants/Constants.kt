package com.tsapiszczak.imgrecognition.constants

import android.Manifest


object Constants  {
    val CAMERA_PERMISSIONS= arrayOf(Manifest.permission.CAMERA)
    val WRITE_EXTERNAL_STORAGE_PERMISSION= arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    const val cam="CameraX "
    const val   REQUEST_CODE_PERMISSIONS=123
    const val  EXTERNAL_REQUEST_CODE_PERMISSIONS=121
    const val  EXTERNAL_WRITE_REQUEST_CODE_PERMISSIONS=122
}