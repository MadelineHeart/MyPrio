package com.madhaus.myprio.data

import org.junit.Assert
import org.junit.Test
import java.util.Calendar

class TimeUtilsTests {

    @Test
    fun testMillisInCurrentDay() {
        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis = System.currentTimeMillis()

        val sumMillis = TimeUtils.getMillisInCurrentDay(System.currentTimeMillis())

        val hours = sumMillis / (1000 * 60 * 60)
        Assert.assertEquals(currentDate.get(Calendar.HOUR_OF_DAY), hours.toInt())

        val minutes = (sumMillis % (1000 * 60 * 60)) / (1000 * 60)
        Assert.assertEquals(currentDate.get(Calendar.MINUTE), minutes.toInt())

        val seconds = (sumMillis % (1000 * 60)) / (1000)
        Assert.assertEquals(currentDate.get(Calendar.SECOND), seconds.toInt())

        val milliseconds = sumMillis % 1000
        Assert.assertEquals(currentDate.get(Calendar.MILLISECOND), milliseconds.toInt())
    }

    @Test
    fun testNormalizeToMidnight() {
        val currentDate = Calendar.getInstance()
        currentDate.timeInMillis = System.currentTimeMillis()

        val date = Calendar.getInstance()
        val normalizedDate = TimeUtils.normalizeToMidnight(System.currentTimeMillis())
        date.timeInMillis = normalizedDate

        Assert.assertEquals(date.get(Calendar.HOUR_OF_DAY), 0)
        Assert.assertEquals(date.get(Calendar.MINUTE), 0)
        Assert.assertEquals(date.get(Calendar.SECOND), 0)
        Assert.assertEquals(date.get(Calendar.MILLISECOND), 0)
        Assert.assertEquals(date.get(Calendar.DAY_OF_YEAR), currentDate.get(Calendar.DAY_OF_YEAR))
    }
}