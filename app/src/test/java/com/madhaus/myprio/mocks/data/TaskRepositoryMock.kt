package com.madhaus.myprio.mocks.data

import com.madhaus.myprio.data.TaskRepository
import org.mockito.Mockito.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.whenever

object TaskRepositoryMock {
    fun getStandardMockedRepo(): TaskRepository {
        val repo = mock(TaskRepository::class.java)

        repo.stub { onBlocking { repo.fetchTasks() }.thenReturn(listOf()) }

        return repo
    }
}