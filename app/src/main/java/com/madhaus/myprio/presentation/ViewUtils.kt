package com.madhaus.myprio.presentation

import com.madhaus.myprio.R

object ViewUtils {
    fun getIconForPriority(priority: Int): Int {
        return when(priority) {
            0 -> R.drawable.ic_prio_0_dm
            1 -> R.drawable.ic_prio_1
            2 -> R.drawable.ic_prio_2
            3 -> R.drawable.ic_prio_3
            4 -> R.drawable.ic_prio_4
            5 -> R.drawable.ic_prio_5
            6 -> R.drawable.ic_prio_6
            7 -> R.drawable.ic_prio_7
            8 -> R.drawable.ic_prio_8
            9 -> R.drawable.ic_prio_9
            else -> R.drawable.ic_prio_0_dm // Should never happen
        }
    }
}