package com.madhaus.myprio.presentation.async

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.domain.PushNotificationUseCase
import com.madhaus.myprio.domain.TaskUseCase
import com.madhaus.myprio.presentation.models.PresoNotification
import java.util.*
import javax.inject.Inject

class MarkDoneService: Service() {

    @Inject
    lateinit var taskUseCase: TaskUseCase
    @Inject
    lateinit var notificationUseCase: PushNotificationUseCase

    init {
        BaseDaggerComponent.injector.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (intent?.extras?.getSerializable(PresoNotification.MARK_TASK_DONE_ID_TAG) as UUID?)?.let {
            markDone(it)
        }
        return START_NOT_STICKY
    }

    private fun markDone(id: UUID) {
        if (taskUseCase.markTaskDone(id, System.currentTimeMillis())) {
            notificationUseCase.dismissNotification(PresoNotification.uuidToNotifIdInt(id))
            stopSelf()
        }
    }

    // Not binding
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}