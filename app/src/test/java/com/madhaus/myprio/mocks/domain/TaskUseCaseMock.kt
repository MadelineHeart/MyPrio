package com.madhaus.myprio.mocks.domain

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.domain.TaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

class TaskUseCaseMock: TaskUseCase {

    override val taskListFlow: MutableStateFlow<List<Task>> = MutableStateFlow(fetchTaskList(System.currentTimeMillis()))

    override fun fetchTaskList(atTime: Long): List<Task> {
        TODO("Not yet implemented")
    }

    override fun fetchTask(taskId: UUID): Task? {
        TODO("Not yet implemented")
    }

    override fun fetchOrCreateTask(taskId: UUID?): Task {
        TODO("Not yet implemented")
    }

    override fun makeOrUpdateTask(newTask: Task): Boolean {
        TODO("Not yet implemented")
    }

    override fun markTaskDone(taskId: UUID, completedTimestamp: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: UUID): Boolean {
        TODO("Not yet implemented")
    }
}