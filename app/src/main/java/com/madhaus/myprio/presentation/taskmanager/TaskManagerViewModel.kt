package com.madhaus.myprio.presentation.taskmanager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.TimeUtils
import com.madhaus.myprio.domain.TaskUseCase
import com.madhaus.myprio.presentation.models.PresoTask
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

interface TaskManagerViewModel {
    val taskLD: LiveData<PresoTask>
    val saveAndExitFlow: Flow<Boolean>
    val errorFlow: Flow<String>

    fun getTask(taskId: UUID?): Task
    fun deleteTask(toDelete: PresoTask): Boolean
    fun saveAndExit(toSave: PresoTask)
    fun cancelAndExit()
}

class TaskManagerViewModelImpl(
    private val taskUseCase: TaskUseCase,
    private val context: Context
) : TaskManagerViewModel {
    override val taskLD = MutableLiveData<PresoTask>()

    override val saveAndExitFlow = MutableSharedFlow<Boolean>(1)
    override val errorFlow = MutableSharedFlow<String>(1)

    override fun getTask(taskId: UUID?): Task {
        val task = taskUseCase.fetchOrCreateTask(taskId)
        taskLD.postValue(PresoTask(task, context))
        return taskUseCase.fetchOrCreateTask(taskId)
    }

    override fun deleteTask(toDelete: PresoTask): Boolean {
        val result = taskUseCase.deleteTask(toDelete.task.id)
        // If we deleted, shouldn't remain on edit page
        if (result) {
            cancelAndExit()
        }
        return result
    }

    override fun saveAndExit(toSave: PresoTask) {
        // If a new task, set proper identifiers
        if (toSave.task.lastCompletedTimestamp == null)
            toSave.task.lastCompletedTimestamp =
                TimeUtils.normalizeToMidnight(System.currentTimeMillis())

        if (toSave.task.verify())
            saveAndExitFlow.tryEmit(taskUseCase.makeOrUpdateTask(toSave.task))
        else
            errorFlow.tryEmit("Task needs more info, cannot save.")
    }

    override fun cancelAndExit() {
        saveAndExitFlow.tryEmit(false)
    }
}