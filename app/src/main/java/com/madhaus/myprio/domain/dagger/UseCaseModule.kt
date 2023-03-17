package com.madhaus.myprio.domain.dagger

import android.app.Application
import com.madhaus.myprio.data.TaskRepository
import com.madhaus.myprio.data.dagger.RepositoryModule
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.domain.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RepositoryModule::class])
class UseCaseModule {

    @Provides
    @Singleton
    fun provideTaskUseCase(taskRepository: TaskRepository): TaskUseCase =
        TaskUseCaseImpl(System.currentTimeMillis(), taskRepository)

    @Provides
    @Singleton
    fun provideSettingsUseCase(settingsRepository: SettingsRepository): SettingsUseCase =
        SettingsUseCaseImpl(settingsRepository)

    @Provides
    @Singleton
    fun provideNotificationUseCase(appContext: Application,
                                   taskUseCase: TaskUseCase,
                                   settingsRepository: SettingsRepository): PushNotificationUseCase =
        PushNotificationUseCaseImpl(appContext, taskUseCase, settingsRepository)
}