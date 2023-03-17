package com.madhaus.myprio.domain

import android.content.Context
import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.mocks.data.SettingsRepositoryMock
import com.madhaus.myprio.mocks.domain.TaskUseCaseMock
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class PushNotificationUseCaseTests {

    var testTime: Long = 0 // Save a single time to avoid runtime distortions
    lateinit var useCase: PushNotificationUseCase

    @Before
    fun setup() {
        testTime = System.currentTimeMillis()

        useCase = PushNotificationUseCaseImpl(mock(Context::class.java),
            TaskUseCaseMock.getStandardMockedRepo(testTime),
            SettingsRepositoryMock.getStandardMockedRepo())
    }

    @Test
    fun testFetchDailyDigest() {
        assertEquals(2, useCase.fetchDailyDigest(testTime).size)
    }

    @Test
    fun testGetTimeToNextDigest() {
        val millisInHour = Task.millisInDay / 24
        val jan10: Long = 1673380800000 // 12:00, Jan 10, 2023
        val april18: Long = 1681826400000 // 7:00, April 18, 2023
        val june3: Long = 1685806200000 // 8:30, April 3, 2023

        assertEquals(millisInHour * 20, useCase.getTimeToNextDigest(jan10))
        assertEquals(millisInHour, useCase.getTimeToNextDigest(april18))
        assertEquals((millisInHour * 23.5).toLong(), useCase.getTimeToNextDigest(june3))
    }
}
