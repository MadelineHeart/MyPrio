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

    init {
        BaseDaggerComponent.injector.inject(this)
        setupManagers()
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

    override fun doWork(): Result {
        if (shouldSend())
            sendDailyDigest()
        startPushWorker()

        return Result.success()
    }

    private fun shouldSend(): Boolean = workerParams.tags.contains(SEND_DIGEST_TAG)

    private fun startPushWorker() {
        // Should only have 1 periodic request running at a time
//        workManager.cancelAllWorkByTag(SEND_DIGEST_TAG)

//        val pushWorkRequest =
//            PeriodicWorkRequestBuilder<PushNotificationWorker>(15, TimeUnit.MINUTES)
//                .setInitialDelay(
//                    pushUseCase.getTimeToNextDigest(System.currentTimeMillis()),
//                    TimeUnit.MILLISECONDS
//                )
//                .addTag(SEND_DIGEST_TAG)
//                .build()

        val pushWorkRequest =
            OneTimeWorkRequestBuilder<PushNotificationWorker>()
                .setInitialDelay(
                    pushUseCase.getTimeToNextDigest(System.currentTimeMillis()),
                    TimeUnit.MILLISECONDS
                )
                .addTag(SEND_DIGEST_TAG)
                .build()

//        workManager.enqueue(pushWorkRequest)
        workManager.enqueueUniqueWork(SEND_DIGEST_TAG,
            ExistingWorkPolicy.REPLACE,
            pushWorkRequest)
    }



    fun sendDailyDigest() {
        runBlocking {
            pushUseCase.fetchDailyDigest(System.currentTimeMillis()).forEach {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(notif: PresoNotification) {
        notifManager.notify(
            UUID.randomUUID().mostSignificantBits.toInt(),
            notif.buildSystemNotif(appContext, SEND_DIGEST_TAG, channelId)
        )
    }

    companion object {
        private val channelId: String = "My_Prio_Notifs"

        private val INIT_PUSH_TAG = "My_Prio_Init_Push"
        private val SEND_DIGEST_TAG = "My_Prio_Daily_Digest"

        fun activate(context: Context) {
            WorkManager
                .getInstance(context)
                .enqueue(
                    OneTimeWorkRequestBuilder<PushNotificationWorker>()
                        .addTag(INIT_PUSH_TAG)
                        .build()
                )
        }
    }
}


