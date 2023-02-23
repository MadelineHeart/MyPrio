package com.madhaus.myprio.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.madhaus.myprio.R

object ViewUtils {
    // TODO make priority 0 at attribute?
    fun getIconForPriority(priority: Int, context: Context): Drawable? {
        val prio0 = context.obtainStyledAttributes(R.styleable.SharedTheme).getDrawable(R.styleable.SharedTheme_priority_0_icon)

        return when(priority) {
            0 -> prio0
            1 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_1)
            2 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_2)
            3 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_3)
            4 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_4)
            5 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_5)
            6 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_6)
            7 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_7)
            8 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_8)
            9 -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_9)
            else -> AppCompatResources.getDrawable(context, R.drawable.ic_prio_0_dm) // Should never happen
        }
    }

//    fun getIconForPriority(priority: Int, context: Context): Int {
//        val prio0 = context?.obtainStyledAttributes(R.styleable.SharedTheme)?.getDrawable(R.attr.priority_0_icon)
//
//        return when(priority) {
//            0 -> R.attr.priority_0_icon
//            1 -> R.drawable.ic_prio_1
//            2 -> R.drawable.ic_prio_2
//            3 -> R.drawable.ic_prio_3
//            4 -> R.drawable.ic_prio_4
//            5 -> R.drawable.ic_prio_5
//            6 -> R.drawable.ic_prio_6
//            7 -> R.drawable.ic_prio_7
//            8 -> R.drawable.ic_prio_8
//            9 -> R.drawable.ic_prio_9
//            else -> R.drawable.ic_prio_0_dm // Should never happen
//        }
//    }
}