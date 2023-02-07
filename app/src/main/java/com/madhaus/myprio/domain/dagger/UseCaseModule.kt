package com.madhaus.myprio.domain.dagger

import com.madhaus.myprio.data.TaskRepository
import com.madhaus.myprio.data.dagger.RepositoryModule
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.domain.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

// TODO we can probably delete this
@Module(includes = [RepositoryModule::class])
class UseCaseModule {

    @Provides
    @Singleton
    fun provideTaskUseCase(taskRepository: TaskRepository): TaskUseCase =
        TaskUseCaseImpl(taskRepository)

    @Provides
    @Singleton
    fun provideSettingsUseCase(settingsRepository: SettingsRepository): SettingsUseCase =
        SettingsUseCaseImpl(settingsRepository)

    @Provides
    @Singleton
    fun provideNotificationUseCase(taskUseCase: TaskUseCase,
                                   settingsRepository: SettingsRepository): PushNotificationUseCase =
        PushNotificationUseCaseImpl(taskUseCase, settingsRepository)
}