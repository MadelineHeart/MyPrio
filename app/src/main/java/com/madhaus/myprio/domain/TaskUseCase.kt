package com.madhaus.myprio.domain

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import javax.inject.Inject

class TaskUseCase
@Inject constructor(private val taskRepository: TaskRepository) {

    private val _taskListFlow: MutableStateFlow<List<Task>> = MutableStateFlow(fetchTaskList(System.currentTimeMillis()))
    val taskListFlow: Flow<List<Task>> = _taskListFlow

    init {
        CoroutineScope(Dispatchers.Main).launch {
            taskRepository.fetchTasksFlow()
                .map { tasks -> tasks.sortedWith(taskSortComparator) }
                .collectLatest {
                _taskListFlow.tryEmit(it)
            }
        }
    }


    fun fetchTaskList(atTime: Long): List<Task> {
        return runBlocking {
            return@runBlocking taskRepository.fetchTasks()
                .sortedWith(taskSortComparator)
        }
    }

    fun fetchTask(taskId: UUID): Task? {
        return runBlocking {
            return@runBlocking taskRepository.fetchTask(taskId)
        }
    }

    fun fetchOrCreateTask(taskId: UUID?): Task {
        return runBlocking {
            return@runBlocking taskId?.let { taskRepository.fetchTask(it) } ?: Task(UUID.randomUUID())
        }
    }

    fun makeOrUpdateTask(newTask: Task): Boolean {
        return runBlocking {
            return@runBlocking taskRepository.saveTask(newTask)
        }
    }

    fun markTaskDone(taskId: UUID, completedTimestamp: Long): Boolean {
        return runBlocking {
            return@runBlocking taskRepository.fetchTask(taskId)?.let { task ->
                task.lastCompletedTimestamp = completedTimestamp
                taskRepository.saveTask(task)
            } ?: false
        }
    }

    fun deleteTask(taskId: UUID): Boolean {
        return runBlocking {
            return@runBlocking taskRepository.deleteTask(taskId)
        }
    }

    companion object {
        private val taskSortComparator = kotlin.Comparator<Task> { o1, o2 ->
            val prioCompare = o2.getPriority(System.currentTimeMillis()) - o1.getPriority(System.currentTimeMillis())
            val longDiff = o1.getRedoTimestamp() - o2.getRedoTimestamp()
            val timeCompare = if (longDiff > 0) 1 else -1 // Gotta do for Int overflow
            return@Comparator if (prioCompare != 0) prioCompare else timeCompare
        }
    }
}