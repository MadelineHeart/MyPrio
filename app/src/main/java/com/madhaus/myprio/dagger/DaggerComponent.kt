package com.madhaus.myprio.dagger

import android.app.Application
import com.madhaus.myprio.data.dagger.RepositoryModule
import com.madhaus.myprio.domain.dagger.UseCaseModule
import com.madhaus.myprio.presentation.PushNotificationWorker
import com.madhaus.myprio.presentation.MainActivity
import com.madhaus.myprio.presentation.settings.SettingsFragment
import com.madhaus.myprio.presentation.taskfeed.TaskFeedAdapter
import com.madhaus.myprio.presentation.taskfeed.TaskFeedFragment
import com.madhaus.myprio.presentation.taskmanager.TaskManagerFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [RepositoryModule::class,
        UseCaseModule::class]
)
interface BaseDaggerComponent {
    @Component.Builder
    interface Builder {
        fun build(): BaseDaggerComponent

        @BindsInstance
        fun application(application: Application): Builder
    }

    companion object {
        lateinit var injector: BaseDaggerComponent
    }

    /** Activities **/
    fun inject(mainActivity: MainActivity)

    /** Fragments **/
    fun inject(taskFeedFragment: TaskFeedFragment)
    fun inject(taskManagerFragment: TaskManagerFragment)
    fun inject(settingsFragment: SettingsFragment)

    /** Adapters **/
    fun inject(taskFeedAdapter: TaskFeedAdapter)

    /** Workers **/
    fun inject(notificationWorker: PushNotificationWorker)
}