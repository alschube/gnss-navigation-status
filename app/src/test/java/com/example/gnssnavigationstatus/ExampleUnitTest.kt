package com.example.gnssnavigationstatus

import com.example.gnssnavigationstatus.model.Satellite
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun satellite_isCorrect() {

        var sat = Satellite(21, "Galileo", 220, 55, 48.5F)
        assertEquals(sat.id, 21)

    }
}