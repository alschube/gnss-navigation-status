package com.example.gnssnavigationstatus

import android.renderscript.Matrix2f
import com.example.gnssnavigationstatus.data.GnssData
import com.example.gnssnavigationstatus.ui.settings.SettingsFragment
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import kotlin.math.cos
import kotlin.math.sin

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun calculateRotation() {
        var degrees: Double = 180.0
        var position = doubleArrayOf(0.0, 1.0)
        var rotation = rotation(degrees, position)

        var x = rotation[0]
        var y = rotation[1]

        assertEquals(0.0, x, 0.0000001)
        assertEquals(-1.0, y, 0.0000001)
    }

    fun rotation(degrees: Double, point: DoubleArray): DoubleArray {
        var radian = Math.toRadians(degrees)
        var rotationMatrix = Array(2) {DoubleArray(2) {0.0} }
        rotationMatrix[0][0] = cos(radian)
        rotationMatrix[0][1] = -sin(radian)
        rotationMatrix[1][0] = sin(radian)
        rotationMatrix[1][1] = cos(radian)

        var result: DoubleArray = doubleArrayOf(0.0, 0.0)

        for(n in rotationMatrix.indices) {
            var sum = 0.0
            for (m in rotationMatrix.indices) {
                sum += rotationMatrix[n][m] * point[m]
            }
            result[n] = sum
        }

        return result
    }

}