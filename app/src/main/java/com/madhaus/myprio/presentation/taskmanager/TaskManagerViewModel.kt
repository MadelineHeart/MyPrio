package com.madhaus.myprio.presentation.taskmanager

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.TimeUtils
import com.madhaus.myprio.domain.TaskUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskManagerViewModel
@Inject constructor(private val taskUseCase: TaskUseCase) {
    private val _saveAndExitFlow = MutableSharedFlow<Boolean>(1)
    val saveAndExitFlow: Flow<Boolean> = _saveAndExitFlow

    private val _errorFlow = MutableSharedFlow<String>(1)
    val errorFlow: Flow<String> = _errorFlow

    fun getTask(taskId: UUID?): Task {
        return taskUseCase.fetchOrCreateTask(taskId)
    }

    fun deleteTask(taskId: UUID): Boolean {
        val result = taskUseCase.deleteTask(taskId)
        // If we deleted, shouldn't remain on edit page
        if (result) { cancelAndExit() }
        return result
    }

    fun saveAndExit(toSave: Task) {
        // If a new task, set proper identifiers
        if (toSave.lastCompletedTimestamp == null)
            toSave.lastCompletedTimestamp = TimeUtils.normalizeToMidnight(System.currentTimeMillis())

        if (toSave.verify())
            _saveAndExitFlow.tryEmit(taskUseCase.makeOrUpdateTask(toSave))
        else
            _errorFlow.tryEmit("Task is malformed, cannot save.")
    }

    fun cancelAndExit() {
        _saveAndExitFlow.tryEmit(false)
    }
}