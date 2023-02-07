package com.madhaus.myprio.presentation

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.work.*
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.domain.PushNotificationUseCase
import com.madhaus.myprio.presentation.models.PresoNotification
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushNotifService : Service() {
    @Inject
    lateinit var pushUseCase: PushNotificationUseCase

    init {
        BaseDaggerComponent.injector.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Should check job ids to make sure nothing is queued
        val pushWorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
            .setInitialDelay(pushUseCase.getTimeToNextDigest(System.currentTimeMillis()), TimeUnit.MILLISECONDS)
            .build()
        WorkManager
            .getInstance(this)
            .enqueue(pushWorkRequest)

        /* TAKE ME OUT, FOR TESTING */
//        testingFunction()
        /* END TAKE OUT */

        return START_NOT_STICKY
    }

    fun testingFunction() {
        Timber.e("PUSH TESTING FUNC CALLED")
        val notifManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notifManager.notificationChannels.firstOrNull { it.id == "My_Prio_Notifs" } == null
        ) {
            val channel = NotificationChannel(
                "My_Prio_Notifs",
                "Channel for all My Prio notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notifManager.createNotificationChannel(channel)
        }
        val contexto = this
        runBlocking {
            pushUseCase.fetchDailyDigest(System.currentTimeMillis()).forEach {
                notifManager.notify(
                    UUID.randomUUID().mostSignificantBits.toInt(),
                    it.buildSystemNotif(contexto, "My_Prio_Daily_Digest", "My_Prio_Notifs")
                )
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Not currently set up for binding
        return null
    }
}

class PushNotificationWorker(private val appContext: Context, workerParams: WorkerParameters) :
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
        sendDailyDigest()

        val pushWorkRequest = OneTimeWorkRequestBuilder<PushNotificationWorker>()
            .setInitialDelay(pushUseCase.getTimeToNextDigest(System.currentTimeMillis()), TimeUnit.MILLISECONDS)
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
}
