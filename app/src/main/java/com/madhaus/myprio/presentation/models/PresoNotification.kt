package com.madhaus.myprio.presentation.models

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.madhaus.myprio.presentation.MainActivity

class PresoNotification(
    val iconRes: Int,
    val title: String,
    val taskPriority: Int,
    val description: String? = null
) {
    fun buildSystemNotif(context: Context, groupTag: String, channelId: String): Notification {
        // Necessary Attributes
        val builder = Notification.Builder(context)
            .setSmallIcon(iconRes)
            .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle(title)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)
        builder.setGroup(groupTag)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId(channelId)

        // Optional Attributes
        description?.let { builder.setContentText(description) }

        return builder.build()
    }

}

/*

 */