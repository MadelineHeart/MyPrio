package com.madhaus.myprio.presentation

import android.app.*
import android.content.Context
import android.os.Build
import androidx.work.*
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.domain.PushNotificationUseCase
import com.madhaus.myprio.presentation.models.PresoNotification
import kotlinx.coroutines.runBlocking
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PushNotificationWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {

    @Inject
    lateinit var pushUseCase: PushNotificationUseCase

    private lateinit var notifManager: NotificationManager
    private lateinit var workManager: WorkManager

    private val channelId: String = "My_Prio_Notifs"
    private val digestGroupTag = "My_Prio_Daily_Digest"

    init {
        BaseDaggerComponent.injector.inject(this)
    }

    override fun doWork(): Result {
        setupManagers()
        if (shouldSend())
            sendDailyDigest()

        val pushWorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
            .setInitialDelay(
                pushUseCase.getTimeToNextDigest(System.currentTimeMillis()),
                TimeUnit.MILLISECONDS
            )
            .addTag(digestGroupTag)
            .build()

        WorkManager
            .getInstance(appContext)
            .enqueue(pushWorkRequest)

        return Result.success()
    }

    private fun setupManagers() {
        // Notifications Manager
        notifManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notifManager.notificationChannels.firstOrNull { it.id == channelId } == null
        ) {
            val channel = NotificationChannel(
                channelId,
                "Channel for all My Prio notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notifManager.createNotificationChannel(channel)
        }

        // Work Manager
        workManager = WorkManager.getInstance(appContext)
    }

    private fun shouldSend(): Boolean =
        workerParams.tags.contains(digestGroupTag) &&
                workManager.getWorkInfosByTag(digestGroupTag).get().isEmpty()

    fun sendDailyDigest() {
        workManager.cancelAllWorkByTag(digestGroupTag)
        runBlocking {
            pushUseCase.fetchDailyDigest(System.currentTimeMillis()).forEach {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(notif: PresoNotification) {
        notifManager.notify(
            UUID.randomUUID().mostSignificantBits.toInt(),
            notif.buildSystemNotif(appContext, digestGroupTag, channelId)
        )
    }

    companion object {
        fun activate(context: Context) {
            val pushWorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
                .build()
            WorkManager
                .getInstance(context)
                .enqueue(pushWorkRequest)
        }
    }
}
