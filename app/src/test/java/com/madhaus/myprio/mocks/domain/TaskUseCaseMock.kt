package com.madhaus.myprio.mocks.domain

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.domain.TaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.whenever
import java.util.*

object TaskUseCaseMock {
    val task1Id = UUID.fromString("18077e7d-c6da-4580-976c-6624917b46c4")
    val task2Id = UUID.fromString("6b78785d-c5fd-454f-a818-9cc9ec11c74d")
    val task3Id = UUID.fromString("fb35138a-a12d-49ff-beb7-98fe87b73afc")
    val task4Id = UUID.fromString("8e6f2478-c33d-4e96-b231-1084cb808625")

    fun getStandardMockedRepo(timestamp: Long): TaskUseCase {
        val taskUseCase = Mockito.mock(TaskUseCase::class.java)
        val task1 = Task(
            task1Id,
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 2,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp
        )
        val task2 = Task(
            task2Id,
            title = "A working task",
            basePriority = 3,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 1)
        )
        val task3 = Task(
            task3Id,
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 3,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 3)
        )
        val task4 = Task(
            task4Id,
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