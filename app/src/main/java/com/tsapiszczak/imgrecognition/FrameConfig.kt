package com.tsapiszczak.imgrecognition

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import kotlin.random.Random


//Class that draws frames around the object
class FrameConfig(context:Context?, var text:String, var frame:Rect):View(context)
{
    lateinit var framePaint: Paint
    lateinit var textPaint: Paint
    val color=Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
    init {
        init()
    }
    private fun init()
    {
        textPaint=Paint()
        textPaint.color= Color.BLACK
        textPaint.textSize=50f
        textPaint.style=Paint.Style.FILL
        framePaint=Paint()
        framePaint.color=color
        framePaint.strokeWidth=10f
        framePaint.style=Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(text,frame.centerX().toFloat(),frame.centerY().toFloat(),textPaint)
        canvas?.drawRect(frame.left.toFloat(),frame.top.toFloat(),frame.right.toFloat(),frame.bottom.toFloat(),framePaint)
    }
}
