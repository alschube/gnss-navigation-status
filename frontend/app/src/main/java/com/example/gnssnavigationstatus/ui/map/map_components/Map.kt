package com.example.gnssnavigationstatus.ui.map.map_components

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.gnssnavigationstatus.data.GnssDataHolder
import kotlin.math.cos
import kotlin.math.sin

class Map(context: Context, width: Int, height: Int) : View(context) {

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

        canvas?.drawPoint(width.toFloat() / 2, height.toFloat() / 2, dotPaint)

        canvas?.drawCircle(width.toFloat() / 2, height.toFloat() / 2, 90f * scale, circlePaint)
        canvas?.drawCircle(width.toFloat() / 2, height.toFloat() / 2, 60f * scale, circlePaint)
        canvas?.drawCircle(width.toFloat() / 2, height.toFloat() / 2, 30f * scale, circlePaint)

        canvas?.drawLine(
            width.toFloat() / 2,
            textSize * 2,
            width.toFloat() / 2,
            height.toFloat() - textSize * 2,
            circlePaint
        )
        canvas?.drawLine(
            textSize * 2,
            height.toFloat() / 2,
            width.toFloat() - textSize * 2,
            height.toFloat() / 2,
            circlePaint
        )

        canvas?.drawText("90°", width.toFloat() / 2, height.toFloat() / 2, textPaint)
        canvas?.drawText("60°", width.toFloat() / 2, (height.toFloat() / 2 - 30 * scale), textPaint)
        canvas?.drawText("30°", width.toFloat() / 2, (height.toFloat() / 2 - 60 * scale), textPaint)
        canvas?.drawText("0°", width.toFloat() / 2, (height.toFloat() / 2 - 90 * scale), textPaint)

        canvas?.drawText(
            "N",
            width.toFloat() / 2 - textSize * 3 / 8,
            textSize + textSize / 2,
            textPaint
        )
        canvas?.drawText("S", width.toFloat() / 2 - textSize * 2 / 8, height - textSize, textPaint)
        canvas?.drawText(
            "O",
            width.toFloat() - textSize * 2,
            height / 2 + textSize * 1 / 3,
            textPaint
        )
        canvas?.drawText("W", textSize, height / 2 + textSize * 1 / 3, textPaint)

        if (!GnssDataHolder.satellites.isNullOrEmpty()) {
            for (sat in GnssDataHolder.satellites!!) {
                val rotatedIdentityVector = rotateIdentityVector(sat.azimut!!)

                val trueIdentityVector = scale(rotatedIdentityVector, this.scale)
                val finalSize = scale(trueIdentityVector, 90 - sat.elevation!!)
                val positionIdentityVector = moveToMidPoint(finalSize)
                canvas?.drawCircle(
                    positionIdentityVector[0].toFloat(),
                    positionIdentityVector[1].toFloat(),
                    5f * scale,
                    circlePaint
                )
            }
        }
    }

    private fun rotateIdentityVector(degrees: Int): DoubleArray {
        var radian = Math.toRadians(degrees.toDouble())
        var identityVector = doubleArrayOf(0.0, -1.0)
        var rotationMatrix = Array(2) { DoubleArray(2) { 0.0 } }
        rotationMatrix[0][0] = cos(radian)
        rotationMatrix[0][1] = -sin(radian)
        rotationMatrix[1][0] = sin(radian)
        rotationMatrix[1][1] = cos(radian)

        var result: DoubleArray = doubleArrayOf(0.0, 0.0)

        for (n in rotationMatrix.indices) {
            var sum = 0.0
            for (m in rotationMatrix.indices) {
                sum += rotationMatrix[n][m] * identityVector[m]
            }
            result[n] = sum
        }

        return result
    }

    private fun moveToMidPoint(position: DoubleArray): DoubleArray {
        return doubleArrayOf(position[0] + width / 2, position[1] + height / 2)
    }

    private fun scale(position: DoubleArray, factor: Int): DoubleArray {
        return doubleArrayOf(position[0] * factor, position[1] * factor)
    }
}