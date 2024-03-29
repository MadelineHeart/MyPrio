package com.madhaus.myprio.presentation.async

import android.app.*
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.*
import com.madhaus.myprio.R
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
//        if (shouldSend())
            sendDailyDigest()
        startPushWorker()

        return Result.success()
    }

    private fun shouldSend(): Boolean = workerParams.tags.contains(SEND_DIGEST_TAG)

    private fun startPushWorker() {
        val pushWorkRequest =
            OneTimeWorkRequestBuilder<PushNotificationWorker>()
                .setInitialDelay(
                    pushUseCase.getTimeToNextDigest(System.currentTimeMillis()),
                    TimeUnit.MILLISECONDS
                )
                .addTag(SEND_DIGEST_TAG)
                .build()

        workManager.enqueueUniqueWork(
            SEND_DIGEST_TAG,
            ExistingWorkPolicy.REPLACE,
            pushWorkRequest)
    }



    fun sendDailyDigest() {
        runBlocking {
            pushUseCase.fetchDailyDigest(System.currentTimeMillis()).forEach {
                sendNotification(it)
            }
            sendSummaryNotification()
        }

    }

    private fun sendNotification(notif: PresoNotification) {
        notifManager.notify(
            PresoNotification.uuidToNotifIdInt(notif.id),
            notif.buildSystemNotif(appContext, SEND_DIGEST_TAG, channelId)
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendSummaryNotification() {
        val summary = Notification.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notif_icon)
            .setStyle(Notification.InboxStyle().setSummaryText("Today's Daily Digest"))
            .setGroup(SEND_DIGEST_TAG)
            .setGroupSummary(true)
            .build()

        notifManager.notify(summaryId.mostSignificantBits.toInt(), summary)
    }

    companion object {
        private val channelId: String = "My_Prio_Notifs"

        private val INIT_PUSH_TAG = "My_Prio_Init_Push"
        private val SEND_DIGEST_TAG = "My_Prio_Daily_Digest"

        // String doesn't really matter, just needs to be consistent
        private val summaryId = UUID.fromString("313701fc-c222-488d-b9c9-432237413155")

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


