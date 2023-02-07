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

interface TaskUseCase {
    val taskListFlow: Flow<List<Task>>

    fun fetchTaskList(atTime: Long): List<Task>
    fun fetchTask(taskId: UUID): Task?
    fun fetchOrCreateTask(taskId: UUID?): Task
    fun makeOrUpdateTask(newTask: Task): Boolean
    fun markTaskDone(taskId: UUID, completedTimestamp: Long): Boolean
    fun deleteTask(taskId: UUID): Boolean
}

class TaskUseCaseImpl(private val taskRepository: TaskRepository): TaskUseCase {

//    private val _taskListFlow: MutableStateFlow<List<Task>> = MutableStateFlow(fetchTaskList(System.currentTimeMillis()))
    override val taskListFlow: MutableStateFlow<List<Task>> = MutableStateFlow(fetchTaskList(System.currentTimeMillis()))

    init {
        CoroutineScope(Dispatchers.Main).launch {
            taskRepository.fetchTasksFlow()
                .map { tasks -> tasks.sortedWith(taskSortComparator) }
                .collectLatest {
                taskListFlow.tryEmit(it)
            }
        }
    }


    override fun fetchTaskList(atTime: Long): List<Task> {
        return runBlocking {
            return@runBlocking taskRepository.fetchTasks()
                .sortedWith(taskSortComparator)
        }
    }

    override fun fetchTask(taskId: UUID): Task? {
        return runBlocking {
            return@runBlocking taskRepository.fetchTask(taskId)
        }
    }

    override fun fetchOrCreateTask(taskId: UUID?): Task {
        return runBlocking {
            return@runBlocking taskId?.let { taskRepository.fetchTask(it) } ?: Task(UUID.randomUUID())
        }
    }

    override fun makeOrUpdateTask(newTask: Task): Boolean {
        return runBlocking {
            return@runBlocking taskRepository.saveTask(newTask)
        }
    }

    override fun markTaskDone(taskId: UUID, completedTimestamp: Long): Boolean {
        return runBlocking {
            return@runBlocking taskRepository.fetchTask(taskId)?.let { task ->
                task.lastCompletedTimestamp = completedTimestamp
                taskRepository.saveTask(task)
            } ?: false
        }
    }

    override fun deleteTask(taskId: UUID): Boolean {
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