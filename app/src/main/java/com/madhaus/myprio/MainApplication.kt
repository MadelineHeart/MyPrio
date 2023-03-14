package com.madhaus.myprio

import android.app.Application
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.dagger.DaggerBaseDaggerComponent
import com.madhaus.myprio.presentation.async.PushNotificationWorker
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        setupDagger()
        startWorker()
    }

    private fun setupDagger() {
        val baseComponent = DaggerBaseDaggerComponent.builder()
            .application(this)
            .build()
        BaseDaggerComponent.injector = baseComponent
    }

    private fun startWorker() {
        PushNotificationWorker.activate(this)
    }
}