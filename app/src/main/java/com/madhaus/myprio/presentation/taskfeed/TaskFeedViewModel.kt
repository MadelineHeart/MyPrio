package com.madhaus.myprio.presentation.taskfeed

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.domain.PushNotificationUseCase
import com.madhaus.myprio.domain.TaskUseCase
import com.madhaus.myprio.presentation.models.PresoNotification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskFeedViewModel
@Inject constructor(
    private val taskUseCase: TaskUseCase,
    private val pushNotificationUseCase: PushNotificationUseCase
) {
    private val _goToManagerFlow = MutableSharedFlow<UUID?>(1)
    val goToManagerFlow: Flow<UUID?> = _goToManagerFlow

    private val _goToSettingsFlow = MutableSharedFlow<Unit>(1)
    val goToSettingsFlow: Flow<Unit> = _goToSettingsFlow

    fun getTaskList(): List<Task> {
        return taskUseCase.fetchTaskList(System.currentTimeMillis())
    }

    fun getTaskListFlow(): Flow<List<Task>> {
        return taskUseCase.taskListFlow
    }

    fun goToNewTask() {
        _goToManagerFlow.tryEmit(null)
    }

    fun goToEditTask(taskId: UUID) {
        _goToManagerFlow.tryEmit(taskId)
    }

    fun goToSettings() {
        _goToSettingsFlow.tryEmit(Unit)
    }

    fun markTaskDone(taskId: UUID) {
        if (taskUseCase.markTaskDone(taskId, System.currentTimeMillis()))
            pushNotificationUseCase.dismissNotification(PresoNotification.uuidToNotifIdInt(taskId))
    }
}
