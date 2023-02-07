package com.madhaus.myprio.dagger

import android.app.Application
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.data.repos.SettingsRepositoryImpl
import com.madhaus.myprio.data.TaskRepository
import com.madhaus.myprio.data.TaskRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideTaskRepository(appContext: Application): TaskRepository {
        return TaskRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(appContext: Application): SettingsRepository {
        return SettingsRepositoryImpl(appContext)
    }
}