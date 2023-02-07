package com.madhaus.myprio.domain

import com.madhaus.myprio.data.repos.SettingsRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class PushNotificationUseCaseTests {

    lateinit var useCase: PushNotificationUseCase

    @Before
    fun setup() {
        // Consider moving these mock creations to objects in mock files
        val repo: SettingsRepository = mock(SettingsRepository::class.java)
        whenever(repo.getDigestMinimumPriority()).thenReturn(1)
        whenever(repo.getDigestSendTimeInMinutes()).thenReturn(480) // 8:00 am

        val taskUseCase = mock(TaskUseCase::class.java)
        whenever(taskUseCase.fetchTaskList(0)).thenReturn(listOf())

        useCase = PushNotificationUseCaseImpl(taskUseCase, repo)
    }

    @Test
    fun checkMe() {
        assertEquals(useCase.fetchDailyDigest(0).size, 0)
    }
}