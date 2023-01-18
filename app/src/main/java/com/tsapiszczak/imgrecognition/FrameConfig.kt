package com.tsapiszczak.imgrecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import org.jetbrains.kotlinx.dl.visualization.R
import kotlin.random.Random


//Class that draws frames around the object
class FrameConfig()
{
    lateinit var framePaint: Paint
    lateinit var textPaint:TextPaint
    val color=Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    init {
        init()
    }
    private fun init()
    {
        textPaint=TextPaint()
        textPaint.color= Color.BLACK
        textPaint.textSize=50f
        textPaint.style=Paint.Style.FILL

        framePaint=Paint()
        framePaint.color=color
        framePaint.strokeWidth=10f
        framePaint.style=Paint.Style.STROKE
       // framePaint.strokeWidth=resources.getDimensionPixelSize.toFloat()
    }
}
