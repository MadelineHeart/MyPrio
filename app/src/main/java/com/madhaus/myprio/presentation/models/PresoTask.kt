package com.madhaus.myprio.presentation.models

import android.content.Context
import com.madhaus.myprio.R
import com.madhaus.myprio.data.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** Put all purely view concerned functions here **/
class PresoTask(private val context: Context, task: Task) : Task(
    task.id,
    task.title,
    task.basePriority,
    task.description,
    task.daysToRepeat,
    task.escalateBy,
    task.daysToEscalate,
    task.lastCompletedTimestamp
) {
    var displayBasePriority: String
        get() = "$basePriority"
        set(value) {
            basePriority = value.toIntOrNull() ?: 1
        }

    var displayDaysToRepeat: String
        get() = daysToRepeat?.let { "$it" } ?: ""
        set(value) {
            daysToRepeat = value.toIntOrNull()
        }

    var displayDaysToEscalate: String
        get() = daysToEscalate?.let { "$it" } ?: ""
        set(value) {
            daysToEscalate = value.toIntOrNull()
        }

    var displayEscalateBy: String
        get() = "$escalateBy"
        set(value) {
            escalateBy = value.toIntOrNull() ?: 0
        }

    fun getLastCompletedDate(): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return lastCompletedTimestamp?.let { "Last completed on: ${formatter.format(Date(it))}" }
            ?: "Malformed Task"
    }

    fun getActivationTime(forTime: Long): String {
        val millisInDay: Long = TimeUnit.DAYS.toMillis(1)
        val repeatTimestamp = millisInDay * (daysToRepeat ?: 0)
        val timeDelta = forTime - (lastCompletedTimestamp ?: 0)
        return if (repeatTimestamp > timeDelta) {
            "${TimeUnit.MILLISECONDS.toDays(repeatTimestamp - timeDelta)} days till reactivation"
        } else {
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            lastCompletedTimestamp?.let { "Activated on: ${formatter.format(Date(it + repeatTimestamp))}" }
                ?: "Malformed Task"
        }
    }

    fun getItemBackground(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledViewBackground, 0)
        else
            theme.getColor(R.styleable.SharedTheme_activeViewBackground, 0)
        theme.recycle()
        return output
    }

    fun getItemMainTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledMainText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_mainText, 0)
        theme.recycle()
        return output
    }

    fun getItemSubTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledSubText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_subText, 0)
        theme.recycle()
        return output
    }

    fun getPriorityColor(forTime: Long): Int {
        return when (getPriority(forTime)) {
            0 -> R.color.no_priority
            in 1..3 -> R.color.low_priority
            in 4..6 -> R.color.medium_priority
            in 7..8 -> R.color.high_priority
            9 -> R.color.max_priority
            else -> R.color.no_priority
        }
    }
}