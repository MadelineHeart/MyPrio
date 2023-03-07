package com.madhaus.myprio.presentation.models

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import com.madhaus.myprio.R
import com.madhaus.myprio.presentation.MainActivity
import com.madhaus.myprio.presentation.ViewUtils

class PresoNotification(
    val title: String,
    val taskPriority: Int,
    val description: String? = null
) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun buildSystemNotif(context: Context, groupTag: String, channelId: String): Notification {
        // TODO make custom notif to mark tasks done from notif

        val yourBitmap = ViewUtils.getIconForPriority(taskPriority, context)!!.toBitmap()
        val notificationLayoutExpanded = RemoteViews(context.packageName, R.layout.notification_tray_view)

        val builder = Notification.Builder(context)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle(title)
//            .setLargeIcon(yourBitmap)
            .setStyle(Notification.DecoratedCustomViewStyle())
            .setCustomBigContentView(notificationLayoutExpanded)
//            .setCustomContentView(notificationLayoutExpanded)

        // Necessary Attributes
//        val builder = Notification.Builder(context)
//            .setSmallIcon(R.drawable.ic_notif_icon)
//            .setPriority(Notification.PRIORITY_MAX)
//            .setContentTitle("Prio ${taskPriority}: $title")
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