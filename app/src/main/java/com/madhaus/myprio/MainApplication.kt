package com.madhaus.myprio

import android.app.Application
import android.content.Intent
import com.madhaus.myprio.dagger.BaseDaggerComponent
import com.madhaus.myprio.dagger.DaggerBaseDaggerComponent
import com.madhaus.myprio.data.TimeUtils
import com.madhaus.myprio.presentation.PushNotifService
import timber.log.Timber
import java.util.Calendar

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        setupDagger()
        startServices()
    }

    private fun setupDagger() {
        val baseComponent = DaggerBaseDaggerComponent.builder()
            .application(this)
            .build()
        BaseDaggerComponent.injector = baseComponent
    }

    private fun startServices() {
        startService(Intent(this, PushNotifService::class.java))
    }
}