package com.madhaus.myprio.mocks.data

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.TaskRepository
import com.madhaus.myprio.mocks.domain.TaskUseCaseMock
import org.mockito.Mockito.mock
import org.mockito.kotlin.stub
import java.util.*

object TaskRepositoryMock {
    val task1Id = UUID.fromString("18077e7d-c6da-4580-976c-6624917b46c4")
    val task2Id = UUID.fromString("6b78785d-c5fd-454f-a818-9cc9ec11c74d")
    val task3Id = UUID.fromString("fb35138a-a12d-49ff-beb7-98fe87b73afc")
    val task4Id = UUID.fromString("8e6f2478-c33d-4e96-b231-1084cb808625")

    val notIncludedId = UUID.fromString("36bb89ef-f2b1-487f-a66b-0bf402df0259")

    fun getStandardMockedRepo(timestamp: Long): TaskRepository {
        val repo = mock(TaskRepository::class.java)

        var task1: Task? = Task(
            TaskUseCaseMock.task1Id,
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 2,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp
        )
        var task2 = Task(
            TaskUseCaseMock.task2Id,
            title = "A working task",
            basePriority = 3,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 1)
        )
        var task3 = Task(
            TaskUseCaseMock.task3Id,
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 3,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 3)
        )
        var task4 = Task(
            TaskUseCaseMock.task4Id,
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 1,
            lastCompletedTimestamp = timestamp - (Task.millisInDay * 4)
        )

        val taskList = listOf(task1!!, task2, task3, task4)

        repo.stub { onBlocking { repo.fetchTasks() }.thenReturn(taskList) }
        repo.stub { onBlocking { repo.fetchTask(task1Id) }.thenReturn(task1) }
        repo.stub {
            onBlocking { repo.deleteTask(task1Id) }.then {
                repo.stub { onBlocking { repo.fetchTask(task1Id) }.thenReturn(null) }

                true
            }
        }
        repo.stub { onBlocking { repo.deleteTask(notIncludedId) }.thenReturn(false) }
        repo.stub { onBlocking { repo.saveTask(task1) }.thenReturn(true) }

        return repo
    }
}