package com.example.gnssnavigationstatus.ui.map.map_components

import android.content.Context
import android.graphics.*
import android.view.View

class Map(context:Context, width: Int, height: Int) : View(context) {

    var scale: Int = 5
    var textSize: Float = 50f

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val dotPaint = Paint()
        dotPaint.setColor(Color.BLACK)
        dotPaint.style = Paint.Style.STROKE
        dotPaint.strokeWidth = 6f

        val circlePaint = Paint()
        circlePaint.setColor(Color.BLACK)
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = 3f

        val textPaint = Paint()
        textPaint.setColor(Color.BLACK)
        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 3f
        textPaint.textSize = textSize

        canvas?.drawPoint(width.toFloat()/2, height.toFloat()/2, dotPaint)

        canvas?.drawCircle(width.toFloat()/2, height.toFloat()/2, 90f * scale, circlePaint)
        canvas?.drawCircle(width.toFloat()/2, height.toFloat()/2, 60f * scale, circlePaint)
        canvas?.drawCircle(width.toFloat()/2, height.toFloat()/2, 30f * scale, circlePaint)

        canvas?.drawLine(width.toFloat()/2, textSize * 2, width.toFloat()/2, height.toFloat() - textSize * 2, circlePaint)
        canvas?.drawLine(textSize * 2, height.toFloat()/2, width.toFloat() - textSize * 2, height.toFloat()/2, circlePaint)

        canvas?.drawText("90°", width.toFloat()/2, height.toFloat()/2, textPaint)
        canvas?.drawText("60°", width.toFloat()/2, (height.toFloat()/2 - 30 * scale), textPaint)
        canvas?.drawText("30°", width.toFloat()/2, (height.toFloat()/2 - 60 * scale), textPaint)
        canvas?.drawText("0°", width.toFloat()/2, (height.toFloat()/2 - 90 * scale), textPaint)

        canvas?.drawText("N", width.toFloat()/2 - textSize * 3/8, textSize + textSize /2, textPaint)
        canvas?.drawText("S", width.toFloat()/2 - textSize * 2/8, height - textSize, textPaint)
        canvas?.drawText("O", width.toFloat() - textSize * 2, height/2 + textSize * 1/3, textPaint)
        canvas?.drawText("W", textSize, height/2 + textSize * 1/3, textPaint)
    }
}