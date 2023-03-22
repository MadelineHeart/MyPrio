package com.madhaus.myprio.domain

import com.madhaus.myprio.MainDispatcherRule
import com.madhaus.myprio.data.Task
import com.madhaus.myprio.mocks.data.TaskRepositoryMock
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*

class TaskUseCaseTest {

    var testTime: Long = 0 // Save a single time to avoid runtime distortions
    lateinit var useCase: TaskUseCase

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        testTime = System.currentTimeMillis()

        useCase = TaskUseCaseImpl(testTime, TaskRepositoryMock.getStandardMockedRepo(testTime))
    }

    @Test
    fun fetchTaskList() {
        val outputIdList = useCase.fetchTaskList(testTime).map { it.id }
        val expectedIdList = listOf(TaskRepositoryMock.task4Id, TaskRepositoryMock.task2Id, TaskRepositoryMock.task3Id, TaskRepositoryMock.task1Id)
        assertEquals(expectedIdList, outputIdList)
    }

    @Test
    fun fetchTask() {
        assertEquals(TaskRepositoryMock.task1Id, useCase.fetchTask(TaskRepositoryMock.task1Id)?.id)
        assertEquals(null, useCase.fetchTask(TaskRepositoryMock.notIncludedId))
    }

    @Test
    fun fetchOrCreateTask() {
        assertEquals(TaskRepositoryMock.task1Id, useCase.fetchTask(TaskRepositoryMock.task1Id)?.id)
        assertNotNull(useCase.fetchOrCreateTask(TaskRepositoryMock.notIncludedId))
    }

    @Test
    fun markTaskDone() {
        assertTrue(useCase.markTaskDone(TaskRepositoryMock.task1Id, testTime + Task.millisInDay))

        assertFalse(useCase.markTaskDone(TaskRepositoryMock.notIncludedId, Task.millisInDay))
    }

    @Test
    fun deleteTask() {
        assertNotNull(useCase.fetchTask(TaskRepositoryMock.task1Id))
        assertTrue(useCase.deleteTask(TaskRepositoryMock.task1Id))
        assertNull(useCase.fetchTask(TaskRepositoryMock.task1Id))

        assertFalse(useCase.deleteTask(TaskRepositoryMock.notIncludedId))
    }
}