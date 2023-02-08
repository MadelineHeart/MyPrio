package com.madhaus.myprio.domain

import com.madhaus.myprio.data.Task
import com.madhaus.myprio.data.TimeUtils
import com.madhaus.myprio.data.repos.SettingsRepository
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import java.util.*

class PushNotificationUseCaseTests {

    var testTime: Long = 0 // Save a single time to avoid runtime distortions
    lateinit var useCase: PushNotificationUseCase

    @Before
    fun setup() {
        testTime = System.currentTimeMillis()

        // Consider moving these mock creations to objects in mock files
        val repo: SettingsRepository = mock(SettingsRepository::class.java)
        whenever(repo.getDigestMinimumPriority()).thenReturn(3)
        whenever(repo.getDigestSendTimeInMinutes()).thenReturn(480) // 8:00 am

        val taskUseCase = mock(TaskUseCase::class.java)
        // TODO move this to another class?
        val task1 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 2,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = testTime
        )
        val task2 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 3,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = testTime - (Task.millisInDay * 1)
        )
        val task3 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 3,
            escalateBy = 1,
            daysToEscalate = 4,
            lastCompletedTimestamp = testTime - (Task.millisInDay * 3)
        )
        val task4 = Task(
            UUID.randomUUID(),
            title = "A working task",
            basePriority = 1,
            description = "This is a well formed, fully filled out Task.",
            daysToRepeat = 1,
            escalateBy = 1,
            daysToEscalate = 1,
            lastCompletedTimestamp = testTime - (Task.millisInDay * 3)
        )

        val taskList = listOf(task1, task2, task3, task4)
        whenever(taskUseCase.fetchTaskList(testTime)).thenReturn(taskList)

        useCase = PushNotificationUseCaseImpl(taskUseCase, repo)
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
