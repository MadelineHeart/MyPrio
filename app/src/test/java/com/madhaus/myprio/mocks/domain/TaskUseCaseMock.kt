package com.madhaus.myprio.mocks.domain

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.domain.TaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import java.util.*

object TaskUseCaseMock {
    fun getStandardMockedRepo(timestamp: Long): TaskUseCase {
        val taskUseCase = Mockito.mock(TaskUseCase::class.java)
        // TODO move this to another class?
        val task1 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 2,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp
        )
        val task2 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 3,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 1)
        )
        val task3 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 3,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 3)
        )
        val task4 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 1,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 3)
        )

        val taskList = listOf(task1, task2, task3, task4)
        whenever(taskUseCase.fetchTaskList(timestamp)).thenReturn(taskList)
        return taskUseCase
    }
}