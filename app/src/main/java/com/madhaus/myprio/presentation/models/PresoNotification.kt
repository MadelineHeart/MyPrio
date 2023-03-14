package com.madhaus.myprio.presentation.models

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDeepLinkBuilder
import com.madhaus.myprio.R
import com.madhaus.myprio.presentation.MainActivity
import com.madhaus.myprio.presentation.ViewUtils
import com.madhaus.myprio.presentation.async.MarkDoneService
import com.madhaus.myprio.presentation.taskmanager.TaskManagerFragment
import java.util.*

class PresoNotification(
    val id: UUID,
    val title: String,
    val taskPriority: Int,
    val description: String? = null
) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun buildSystemNotif(context: Context, groupTag: String, channelId: String): Notification {

        val yourBitmap = ViewUtils.getIconForPriority(taskPriority, context)!!.toBitmap()
        val notificationHeader = RemoteViews(context.packageName, R.layout.notification_header)
        val notificationTray = RemoteViews(context.packageName, R.layout.notification_tray_view)

        notificationHeader.setImageViewBitmap(R.id.notification_prio_image, yourBitmap)
        notificationHeader.setTextViewText(R.id.notification_title, title)

        notificationTray.setImageViewBitmap(R.id.notification_prio_image, yourBitmap)
        notificationTray.setTextViewText(R.id.notification_title, title)
        description?.let {
            notificationTray.setViewVisibility(R.id.notification_description, View.VISIBLE)
            notificationTray.setTextViewText(R.id.notification_description, description)
        }

        val builder = Notification.Builder(context)
            .setGroup(groupTag)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setPriority(Notification.PRIORITY_MAX)
            .setCustomContentView(notificationHeader)
            .setCustomBigContentView(notificationTray)

        // General tap intents
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val tapPendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, tapIntent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(tapPendingIntent)

        // Edit Button intent
        val editArgs =
            Bundle().apply { putSerializable(TaskManagerFragment.MANAGER_TASK_ID_TAG, id) }
        val editPendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.TaskManagerFragment)
            .setArguments(editArgs)
            .createPendingIntent()
        notificationTray.setOnClickPendingIntent(R.id.editButton, editPendingIntent)

        // Done Button intent
        val doneIntent = Intent(context, MarkDoneService::class.java)
            .putExtra(MARK_TASK_DONE_ID_TAG, id)
        notificationTray.setOnClickPendingIntent(
            R.id.doneButton,
            PendingIntent.getService(
                context, uuidToNotifIdInt(id), doneIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId(channelId)

        return builder.build()
    }

    companion object {
        const val MARK_TASK_DONE_ID_TAG = "My_Prio_Mark_Task_Done_Id"

        fun uuidToNotifIdInt(uuid: UUID): Int = uuid.mostSignificantBits.toInt()
    }
}