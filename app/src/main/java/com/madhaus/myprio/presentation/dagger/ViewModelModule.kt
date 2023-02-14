package com.madhaus.myprio.presentation.dagger

import android.app.Application
import com.madhaus.myprio.domain.TaskUseCase
import com.madhaus.myprio.domain.dagger.UseCaseModule
import com.madhaus.myprio.presentation.taskmanager.TaskManagerViewModel
import com.madhaus.myprio.presentation.taskmanager.TaskManagerViewModelImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [UseCaseModule::class])
class ViewModelModule {
    @Provides
    @Singleton
    fun provideTaskManagerVM(taskUseCase: TaskUseCase, appContext: Application): TaskManagerViewModel =
        TaskManagerViewModelImpl(taskUseCase, appContext)
}