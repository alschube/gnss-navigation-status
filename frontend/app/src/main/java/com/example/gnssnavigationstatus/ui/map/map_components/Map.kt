package com.example.gnssnavigationstatus.ui.map.map_components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.View
import com.example.gnssnavigationstatus.R
import com.example.gnssnavigationstatus.data.GnssDataHolder
import kotlin.math.cos
import kotlin.math.sin
import android.os.Build
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi


/**
 * This class is responsible for drawing the map
 *
 * @constructor
 * @param context the application context
 * @param width the width of the screen
 * @param height the height of the screen
 */
@SuppressLint("ViewConstructor")
@RequiresApi(Build.VERSION_CODES.R)
class Map(context: Context, width: Int, height: Int) : View(context) {

    private var wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var metrics:WindowMetrics = wm.currentWindowMetrics
    private var viewRight = metrics.bounds.right

    /** create some variables used for calculation*/
    private var scale: Int = viewRight / 250
    private var textLabelY:Float = 16f
    private var textLabelX:Float = 12.5f
    private var scaledTextSize:Float = viewRight / 30f
    private val thinStroke: Float = 3f
    private val thickStroke: Float = 6f
    private var centerX: Float = width.toFloat() / 2
    private var centerY: Float = height.toFloat() / 2
    private val rotationAngle:Int = 90
    private val thirtyDegrees:Float = 30f
    private val sixtyDegrees:Float = 60f
    private val ninetyDegrees:Float = 90f
    private val hundredScaled:Float = 100f * scale
    private val scaledTextDistance:Float = 110f * scale

    /** create some more colors*/
    private val orange: Int = Color.rgb(251, 140, 0)
    private val lGreen: Int = Color.rgb(109, 201, 69)
    private val lBlue: Int = Color.rgb(93, 194, 244)

    /** create a paint for each type of object */
    private var dotPaint = Paint()
    private var circlePaint = Paint()
    private var textPaint = Paint()
    private var boldTextPaint = Paint()
    private var satellitePaint = Paint()
    private var satIdTextPaint = Paint()

    /**
     * This Method is called once on creating the fragment
     *
     * @param canvas the map area
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        this.centerX = width.toFloat() / 2
        this.centerY = height.toFloat() / 2

        // defines the style of dots
        dotPaint = createPaint(Color.BLACK, thickStroke, Paint.Style.STROKE)

        //defines the style of circles
        circlePaint = createPaint(Color.GRAY, thinStroke, Paint.Style.STROKE)

        //defines the style of texts
        textPaint = createPaint(Color.BLACK, thinStroke, Paint.Style.FILL)
        textPaint.textSize = scaledTextSize

        //defines the style of bold texts
        boldTextPaint = createPaint(Color.BLACK, thinStroke, Paint.Style.FILL_AND_STROKE)
        boldTextPaint.textSize = scaledTextSize

        //defines the style of satelliteIdentifier
        satIdTextPaint = createPaint(Color.BLACK, 8f, Paint.Style.FILL)
        satIdTextPaint.textSize = scaledTextSize

        //defines the style of the satellites
        satellitePaint = createPaint(Color.TRANSPARENT, thinStroke, Paint.Style.FILL)

        //draws the coordinate system
        canvas?.drawPoint(centerX, centerY, dotPaint) //midpoint
        canvas?.drawCircle(centerX, centerY, ninetyDegrees * scale, circlePaint)
        canvas?.drawCircle(centerX, centerY, sixtyDegrees * scale, circlePaint)
        canvas?.drawCircle(centerX, centerY, thirtyDegrees * scale, circlePaint)
        canvas?.drawLine(centerX,
            centerY + hundredScaled, centerX, centerY - hundredScaled, circlePaint) //vertical line
        canvas?.drawLine(centerX + hundredScaled, centerY, centerX - hundredScaled, centerY, circlePaint) //horizontal line

        //labels the coordinate system
        canvas?.drawText("90°", centerX, centerY, textPaint)
        canvas?.drawText("60°", centerX, (centerY - thirtyDegrees * scale), textPaint)
        canvas?.drawText("30°", centerX, (centerY - sixtyDegrees * scale), textPaint)
        canvas?.drawText("0°", centerX, (centerY - ninetyDegrees * scale), textPaint)

        canvas?.drawText("N", centerX - textLabelX, centerY - scaledTextDistance, boldTextPaint)
        canvas?.drawText("S", centerX - textLabelX, centerY + scaledTextDistance, boldTextPaint)
        canvas?.drawText("O", centerX + scaledTextDistance, centerY + textLabelY, boldTextPaint)
        canvas?.drawText("W", centerX - scaledTextDistance, centerY + textLabelY, boldTextPaint)

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
                    context.getString(R.string.GAL_text) -> satellitePaint.color = lBlue
                    context.getString(R.string.GLO_text) -> satellitePaint.color = lGreen
                    context.getString(R.string.BDS_text) -> satellitePaint.color = Color.RED
                    context.getString(R.string.GPS_text) -> satellitePaint.color = orange
                }

                //draw the satellite
                val positionIdentityVector = moveToMidPoint(finalSize)
                canvas?.drawCircle(
                    positionIdentityVector[0].toFloat(),
                    positionIdentityVector[1].toFloat(),
                    5f * scale,
                    satellitePaint
                )
                //draw the satellite identifier
                canvas?.drawText(sat.satelliteIdentifier.toString(), positionIdentityVector[0].toFloat() - 25, positionIdentityVector[1].toFloat()+10, satIdTextPaint)
            }
        }
    }

    /**
     * Method for rotating coordinates with a rotation matrix
     *
     * @param degrees the angle to rotate around the origin
     * @return the rotated vector of a {@link SatelliteData}
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

    /**
     * Method for moving the points to the correct position in the coordinate system
     *
     * @param position the satellite position
     * @return new position of satellite
     */
    private fun moveToMidPoint(position: DoubleArray): DoubleArray {
        return doubleArrayOf(position[0] + centerX, position[1] + centerY)
    }

    /**
     * Method for scaling by a factor
     *
     * @param position the satellite position
     * @param factor factor the vector should be scaled by
     * @return the scaled position
     */
    private fun scale(position: DoubleArray, factor: Int): DoubleArray {
        return doubleArrayOf(position[0] * factor, position[1] * factor)
    }

    /**
     * Method for creating a new Paint with a specific style
     *
     * @param color the color in which the object should be drawn in
     * @param strWidth the stroke width the object should be drawn with
     * @param fillType the filltype of the object, e.g. FILL or STROKE
     * @return the created Paint
     */
    private fun createPaint(color: Int, strWidth: Float, fillType: Paint.Style): Paint {
        val tempPaint = Paint()
        tempPaint.color = color
        tempPaint.style = fillType
        tempPaint.strokeWidth = strWidth
        return tempPaint

    }
}