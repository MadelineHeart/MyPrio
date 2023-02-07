package com.madhaus.myprio.data

import java.util.*

object TimeUtils {
    fun getMillisInCurrentDay(timeMillis: Long): Long {
        val date = Calendar.getInstance()
        date.timeInMillis = timeMillis
        return ((date.get(Calendar.HOUR_OF_DAY) * (60 * 60 * 1000)) +
                (date.get(Calendar.MINUTE) * (60 * 1000)) +
                (date.get(Calendar.SECOND) * (1000)) +
                date.get(Calendar.MILLISECOND)).toLong()
    }

    // Returns timestamp of midnight on the day timeMillis describes
    fun normalizeToMidnight(timeMillis: Long): Long {
        val date = Calendar.getInstance()
        date.timeInMillis = timeMillis
        val millisSinceMidnight = (date.get(Calendar.HOUR_OF_DAY) * (60 * 60 * 1000)) +
                (date.get(Calendar.MINUTE) * (60 * 1000)) +
                (date.get(Calendar.SECOND) * (1000)) +
                date.get(Calendar.MILLISECOND)
        return timeMillis - millisSinceMidnight
    }
}