package com.example.gnssnavigationstatus.ui.map.map_components

import android.content.Context
import android.graphics.*
import android.view.View
import com.example.gnssnavigationstatus.data.GnssDataHolder
import kotlin.math.cos
import kotlin.math.sin

/*
This class draws the map
 */
class Map(context: Context, width: Int, height: Int) : View(context) {

    //create some variables used for calculation
    private var scale: Int = 5
    private var textSize: Float = 50f
    private var textSizeDoubled: Float = textSize * 2
    private var textSizeQuartered: Float = textSize * 2 / 8
    private val thinStroke: Float = 3f
    private val thickStroke: Float = 6f
    private var widthHalved: Float = width.toFloat() / 2
    private var heightHalved: Float = height.toFloat() / 2
    private val rotationAngle:Int = 90
    private val thirtyDegrees:Float = 30f
    private val sixtyDegrees:Float = 60f
    private val ninetyDegrees:Float = 90f

    //create two more colors
    private val orange: Int = Color.rgb(251, 140, 0)
    private val dkgreen: Int = Color.rgb(0, 137, 123)
    private val lgreen: Int = Color.rgb(43, 189, 101)
    private val lblue: Int = Color.rgb(0, 228, 255)

    //create a paint for each type
    private var dotPaint = Paint()
    private var circlePaint = Paint()
    private var textPaint = Paint()
    private var satellitePaint = Paint()
    private var satIdTextPaint = Paint()


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.widthHalved = width.toFloat() / 2
        this.heightHalved = height.toFloat() / 2

        // defines the style of dots
        dotPaint = createPaint(Color.BLACK, thickStroke, Paint.Style.STROKE)

        //defines the style of circles
        circlePaint = createPaint(Color.BLACK, thinStroke, Paint.Style.STROKE)

        //defines the style of texts
        textPaint = createPaint(Color.BLACK, thinStroke, Paint.Style.FILL)
        textPaint.textSize = textSize

        //defines the style of satelliteIdentifier
        satIdTextPaint = createPaint(Color.BLACK, 8f, Paint.Style.FILL)
        satIdTextPaint.textSize = 40f

        //defines the style of the satellites
        satellitePaint = createPaint(Color.TRANSPARENT, thinStroke, Paint.Style.FILL)

        //draws the coordinate system
        canvas?.drawPoint(widthHalved, heightHalved, dotPaint) //midpoint
        canvas?.drawCircle(widthHalved, heightHalved, ninetyDegrees * scale, circlePaint)
        canvas?.drawCircle(widthHalved, heightHalved, sixtyDegrees * scale, circlePaint)
        canvas?.drawCircle(widthHalved, heightHalved, thirtyDegrees * scale, circlePaint)
        canvas?.drawLine(widthHalved, textSizeDoubled, widthHalved, height - textSizeDoubled, circlePaint) //vertical line
        canvas?.drawLine(textSizeDoubled, heightHalved, width - textSize * 2.5f, heightHalved, circlePaint) //horizontal line

        //labels the coordinate system
        canvas?.drawText("90°", widthHalved, heightHalved, textPaint)
        canvas?.drawText("60°", widthHalved, (heightHalved - thirtyDegrees * scale), textPaint)
        canvas?.drawText("30°", widthHalved, (heightHalved - sixtyDegrees * scale), textPaint)
        canvas?.drawText("0°", widthHalved, (heightHalved - ninetyDegrees * scale), textPaint)

        canvas?.drawText("N", widthHalved - textSize * 3 / 8, textSize + textSize / 2, textPaint)
        canvas?.drawText("S", widthHalved - textSizeQuartered, height - textSize, textPaint)
        canvas?.drawText("O", width - textSizeDoubled, heightHalved + textSize * 1 / 3, textPaint)
        canvas?.drawText("W", textSize, height / 2 + textSize * 1 / 3, textPaint)

        // draw the satellites
        // first check if data is given
        if (!GnssDataHolder.satellites.isNullOrEmpty()) {
            for (sat in GnssDataHolder.satellites!!) {
                //for each satellite rotate its coordinates around 90 degrees and scale to coordinate system
                val rotatedIdentityVector = rotateIdentityVector(sat.azimut!!)
                val trueIdentityVector = scale(rotatedIdentityVector, this.scale)
                val finalSize = scale(trueIdentityVector, rotationAngle - sat.elevation!!)

                // find correct satellite type and the according color
                when (sat.type) {
                    "Galileo" -> satellitePaint.color = Color.BLUE
                    "GLONASS" -> satellitePaint.color = dkgreen
                    "BeiDou" -> satellitePaint.color = Color.RED
                    "GPS" -> satellitePaint.color = orange
                }

                //draw the satellite
                val positionIdentityVector = moveToMidPoint(finalSize)
                canvas?.drawCircle(
                    positionIdentityVector[0].toFloat(),
                    positionIdentityVector[1].toFloat(),
                    5f * scale,
                    satellitePaint
                )
                canvas?.drawText(sat.satelliteIdentifier.toString(), positionIdentityVector[0].toFloat() - 25, positionIdentityVector[1].toFloat()+10, satIdTextPaint)
            }
        }
    }

    /*
    Method for rotating coordinates with a rotation matrix
     */
    private fun rotateIdentityVector(degrees: Int): DoubleArray {
        val radian = Math.toRadians(degrees.toDouble())
        val identityVector = doubleArrayOf(0.0, -1.0)
        val rotationMatrix = Array(2) { DoubleArray(2) { 0.0 } }
        rotationMatrix[0][0] = cos(radian)
        rotationMatrix[0][1] = -sin(radian)
        rotationMatrix[1][0] = sin(radian)
        rotationMatrix[1][1] = cos(radian)

        val result: DoubleArray = doubleArrayOf(0.0, 0.0)

        for (n in rotationMatrix.indices) {
            var sum = 0.0
            for (m in rotationMatrix.indices) {
                sum += rotationMatrix[n][m] * identityVector[m]
            }
            result[n] = sum
        }

        return result
    }

    /*
    Method for moving the points to the correct position in the coordinate system
     */
    private fun moveToMidPoint(position: DoubleArray): DoubleArray {
        return doubleArrayOf(position[0] + widthHalved, position[1] + heightHalved)
    }

    /*
    Method for scaling by a factor
     */
    private fun scale(position: DoubleArray, factor: Int): DoubleArray {
        return doubleArrayOf(position[0] * factor, position[1] * factor)
    }

    /*
    Method for creating a new Paint with a specific style
     */
    private fun createPaint(color: Int, strWidth: Float, fillType: Paint.Style): Paint {
        val tempPaint = Paint()
        tempPaint.color = color
        tempPaint.style = fillType
        tempPaint.strokeWidth = strWidth
        return tempPaint

    }
}