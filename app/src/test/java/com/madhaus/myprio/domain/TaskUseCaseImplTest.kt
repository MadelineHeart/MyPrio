package com.madhaus.myprio.domain

import com.madhaus.myprio.MainDispatcherRule
import com.madhaus.myprio.mocks.data.TaskRepositoryMock
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskUseCaseImplTest {

    var testTime: Long = 0 // Save a single time to avoid runtime distortions
    lateinit var useCase: TaskUseCase

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        testTime = System.currentTimeMillis()

        useCase = TaskUseCaseImpl(testTime, TaskRepositoryMock.getStandardMockedRepo())
    }

    @Test
    fun fetchTaskList() {
        assert(false)
    }

    @Test
    fun fetchTask() {
        assert(false)
    }

    @Test
    fun fetchOrCreateTask() {
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