package com.madhaus.myprio.presentation.models

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.madhaus.myprio.R
import com.madhaus.myprio.BR
import com.madhaus.myprio.data.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** Put all purely view concerned functions here **/
class PresoTask(
    private val task: Task,
    private val context: Context,
    val isExampleCell: Boolean = false
) : BaseObservable() {
    @get:Bindable
    var title: String = task.title
        set(value) {
            field = value
            task.title = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var displayBasePriority: String = "${task.basePriority}"
        set(value) {
            field = value
            task.basePriority = value.toIntOrNull() ?: -1
            notifyPropertyChanged(BR.displayBasePriority)
        }

    @get:Bindable
    var description: String? = task.description
        set(value) {
            field = value
            task.description = value
            notifyPropertyChanged(BR.description)
        }

    @get:Bindable
    var displayDaysToRepeat: String = task.daysToRepeat?.let { "$it" } ?: ""
        set(value) {
            field = value
            task.daysToRepeat = value.toIntOrNull()
            notifyPropertyChanged(BR.displayDaysToRepeat)
        }

    @get:Bindable
    var displayDaysToEscalate: String = task.daysToEscalate?.let { "$it" } ?: ""
        set(value) {
            field = value
            task.daysToEscalate = value.toIntOrNull()
            notifyPropertyChanged(BR.displayDaysToEscalate)
        }

    @get:Bindable
    var displayEscalateBy: String = "${task.escalateBy}"
        set(value) {
            field = value
            task.escalateBy = value.toIntOrNull() ?: -1
            notifyPropertyChanged(BR.displayEscalateBy)
        }

    fun buildTask(): Task? {
        return if (task.verify()) task else null
    }

    fun getLastCompletedDate(): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return task.lastCompletedTimestamp?.let { "Last completed on: ${formatter.format(Date(it))}" }
            ?: "Malformed Task"
    }

    fun getActivationTime(forTime: Long): String {
        val millisInDay: Long = TimeUnit.DAYS.toMillis(1)
        val repeatTimestamp = millisInDay * (task.daysToRepeat ?: 0)
        val timeDelta = forTime - (task.lastCompletedTimestamp ?: 0)
        return if (repeatTimestamp > timeDelta) {
            "${TimeUnit.MILLISECONDS.toDays(repeatTimestamp - timeDelta) + 1} days till reactivation"
        } else {
            val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
            task.lastCompletedTimestamp?.let { "Activated on: ${formatter.format(Date(it + repeatTimestamp))}" }
                ?: "Malformed Task"
        }
    }

    fun getItemBackground(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledViewBackground, 0)
        else
            theme.getColor(R.styleable.SharedTheme_activeViewBackground, 0)
        theme.recycle()
        return output
    }

    fun getItemMainTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledMainText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_mainText, 0)
        theme.recycle()
        return output
    }

    fun getItemSubTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0)
            theme.getColor(R.styleable.SharedTheme_disabledSubText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_subText, 0)
        theme.recycle()
        return output
    }

    fun getPriority(forTime: Long): Int =
        if (isExampleCell)
            task.basePriority
        else
            task.getPriority(forTime)

    fun getPriorityColor(forTime: Long): Int {
        return when (task.getPriority(forTime)) {
            0 -> R.color.no_priority
            in 1..3 -> R.color.low_priority
            in 4..6 -> R.color.medium_priority
            in 7..8 -> R.color.high_priority
            9 -> R.color.max_priority
            else -> R.color.no_priority
        }
    }
}