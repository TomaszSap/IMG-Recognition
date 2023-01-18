package com.tsapiszczak.imgrecognition.machinelearning

import android.content.Context
import org.jetbrains.kotlinx.dl.api.inference.FlatShape

interface Prediction {
    val shapes: List<FlatShape<*>>
    val confidence: Float
    fun getText(context: Context): String
}