package com.madhaus.myprio.presentation.models

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.madhaus.myprio.R
import com.madhaus.myprio.BR
import com.madhaus.myprio.data.Task
import com.madhaus.myprio.presentation.ViewUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/** Put all purely view concerned functions here **/
class PresoTask(
    val task: Task,
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

            displayPriorityIcon =
                ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()), context)
            notifyPropertyChanged(BR.displayPriorityIcon)
        }

    @get:Bindable
    var displayPriorityIcon: Drawable? =
        ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()), context)

//    @get:Bindable
//    var displayPriorityIcon: Int =
//        ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()))

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

            displayActivationDate = getActivationTime(System.currentTimeMillis())
            notifyPropertyChanged(BR.displayActivationDate)

            displayPriorityIcon =
                ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()), context)
            notifyPropertyChanged(BR.displayPriorityIcon)
        }

    @get:Bindable
    var displayDaysToEscalate: String = task.daysToEscalate?.let { "$it" } ?: ""
        set(value) {
            field = value
            task.daysToEscalate = value.toIntOrNull()
            notifyPropertyChanged(BR.displayDaysToEscalate)

            displayPriorityIcon =
                ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()), context)
            notifyPropertyChanged(BR.displayPriorityIcon)
        }

    @get:Bindable
    var displayEscalateBy: String = "${task.escalateBy}"
        set(value) {
            field = value
            task.escalateBy = value.toIntOrNull() ?: -1
            notifyPropertyChanged(BR.displayEscalateBy)

            displayPriorityIcon =
                ViewUtils.getIconForPriority(getPriority(System.currentTimeMillis()), context)
            notifyPropertyChanged(BR.displayPriorityIcon)
        }

    @get:Bindable
    var displayLastCompleted: String = getLastCompletedDate()

    @get:Bindable
    var displayActivationDate: String = getActivationTime(System.currentTimeMillis())

    private fun getLastCompletedDate(): String {
        if (isExampleCell) return getExampleLastCompleted()

        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return task.lastCompletedTimestamp?.let { "Last completed on: ${formatter.format(Date(it))}" }
            ?: "Malformed Task"
    }

    // Example defaults time completed to now
    private fun getExampleLastCompleted(): String {
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return "Last completed on: ${formatter.format(Date(task.lastCompletedTimestamp ?: System.currentTimeMillis()))}"
    }

    private fun getActivationTime(forTime: Long): String {
        if (isExampleCell) return getExampleActivationTime(forTime)

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

    private fun getExampleActivationTime(forTime: Long): String {
        if (task.daysToRepeat == null)
            return ""

        val millisInDay: Long = TimeUnit.DAYS.toMillis(1)
        val repeatTimestamp = millisInDay * (task.daysToRepeat ?: 0)
        val timeDelta = forTime - (task.lastCompletedTimestamp ?: 0)
        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())

        return if (repeatTimestamp > timeDelta) {
            "Activated on: ${formatter.format(Date(System.currentTimeMillis() + repeatTimestamp))}"
        } else {
            task.lastCompletedTimestamp?.let { "Activated on: ${formatter.format(Date(it + repeatTimestamp))}" }
                ?: "Activated on: ${formatter.format(Date(System.currentTimeMillis() + repeatTimestamp))}"
        }
    }

    fun getItemBackground(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0 && !isExampleCell)
            theme.getColor(R.styleable.SharedTheme_disabledViewBackground, 0)
        else
            theme.getColor(R.styleable.SharedTheme_activeViewBackground, 0)
        theme.recycle()
        return output
    }

    fun getItemMainTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0 && !isExampleCell)
            theme.getColor(R.styleable.SharedTheme_disabledMainText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_mainText, 0)
        theme.recycle()
        return output
    }

    fun getItemSubTextColor(forTime: Long): Int {
        val theme = context.obtainStyledAttributes(R.styleable.SharedTheme)
        val output = if (task.getPriority(forTime) == 0 && !isExampleCell)
            theme.getColor(R.styleable.SharedTheme_disabledSubText, 0)
        else
            theme.getColor(R.styleable.SharedTheme_subText, 0)
        theme.recycle()
        return output
    }

    private fun getPriority(forTime: Long): Int {
        val hasActivated = task.lastCompletedTimestamp?.let {
            forTime > (it + TimeUnit.DAYS.toMillis((task.daysToRepeat ?: 0).toLong()))
        } ?: false
        return if (isExampleCell && !hasActivated)
            task.basePriority
        else
            task.getPriority(forTime)
    }
}