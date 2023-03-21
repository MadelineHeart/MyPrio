package com.madhaus.myprio.domain

import com.madhaus.myprio.MainDispatcherRule
import com.madhaus.myprio.mocks.data.TaskRepositoryMock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        assertEquals(null, useCase.fetchTask(UUID.fromString(TaskRepositoryMock.notIncludedId.toString())))
    }

    @Test
    fun fetchOrCreateTask() {
        assertEquals(TaskRepositoryMock.task1Id, useCase.fetchTask(TaskRepositoryMock.task1Id)?.id)
        assertNotNull(useCase.fetchOrCreateTask(UUID.fromString(TaskRepositoryMock.notIncludedId.toString())))
    }

    @Test
    fun makeOrUpdateTask() {
    }

    @Test
    fun markTaskDone() {
    }

    @Test
    fun deleteTask() {
    }
}