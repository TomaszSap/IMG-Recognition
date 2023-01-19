package com.tsapiszczak.imgrecognition.machinelearning
import android.content.Context
import org.jetbrains.kotlinx.dl.api.inference.FlatShape

interface ObjectFrameData {
    val shapes: List<FlatShape<*>>
    val confidence: Float
    fun getText(context: Context): String
}