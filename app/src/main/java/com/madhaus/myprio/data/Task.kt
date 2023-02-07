package com.madhaus.myprio.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import java.util.concurrent.TimeUnit

@Entity(tableName = "tasks")
open class Task(
    @PrimaryKey var id: UUID,
    var title: String = "",
    var basePriority: Int = 1,
    var description: String? = null,
    var daysToRepeat: Int? = null,
    var escalateBy: Int = 1,
    var daysToEscalate: Int? = null,
    var lastCompletedTimestamp: Long? = null
) {
    fun getPriority(currentTime: Long): Int {
        // Cannot calculate if not fully formed
        val daysToEscalate = daysToEscalate ?: return -1

        val timeDelta = (currentTime - getRedoTimestamp())
        if (timeDelta < 0) return 0 // Not yet time to redo
        val daysSinceRepeat = max((timeDelta / millisInDay).toInt(), 1)
        val numEscalations = (daysSinceRepeat / daysToEscalate) * escalateBy
        return min(9, basePriority + numEscalations)
    }

    fun getRedoTimestamp(): Long {
        val daysToRepeat = daysToRepeat ?: return -1
        return (lastCompletedTimestamp ?: 0) + (millisInDay * daysToRepeat)
    }

    fun verify(): Boolean {
        return title.isNotBlank() &&
                basePriority >= 0 && basePriority <= 9 &&
                daysToRepeat?.let { it > 0 } ?: false &&
                escalateBy >= 1 && escalateBy <= 9 &&
                daysToEscalate?.let { it > 0 } ?: false &&
                (lastCompletedTimestamp ?: 0) > 0
    }

    companion object {
        val millisInDay: Long = TimeUnit.DAYS.toMillis(1)
    }
}
