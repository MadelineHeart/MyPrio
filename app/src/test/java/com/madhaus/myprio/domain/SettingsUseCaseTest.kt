package com.madhaus.myprio.domain

import com.madhaus.myprio.mocks.data.SettingsRepositoryMock
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class SettingsUseCaseTest {

    lateinit var useCase: SettingsUseCase

    @Before
    fun setup() {
        useCase = SettingsUseCaseImpl(SettingsRepositoryMock.getStandardMockedRepo())
    }

    @Test
    fun getDailyDigestMinPriority() {
        assertEquals(useCase.getDailyDigestMinPriority(), SettingsRepositoryMock.MINIMUM_PRIORITY)
    }

    @Test
    fun setDailyDigestMinPriority() {
        assertTrue(useCase.setDailyDigestMinPriority(1))
        assertTrue(useCase.setDailyDigestMinPriority(4))
        assertTrue(useCase.setDailyDigestMinPriority(9))

        assertFalse(useCase.setDailyDigestMinPriority(0))
        assertFalse(useCase.setDailyDigestMinPriority(-4))
        assertFalse(useCase.setDailyDigestMinPriority(10))
    }

    @Test
    fun getDailyDigestSendTime() {
        val expected = (SettingsRepositoryMock.DIGEST_SEND_TIME / 60) to (SettingsRepositoryMock.DIGEST_SEND_TIME % 60)
        assertEquals(useCase.getDailyDigestSendTime(), expected)
    }

    @Test
    fun setDailyDigestSendTime() {
        assertTrue(useCase.setDailyDigestSendTime(0, 0))
        assertTrue(useCase.setDailyDigestSendTime(9, 30))
        assertTrue(useCase.setDailyDigestSendTime(23, 60))

        assertFalse(useCase.setDailyDigestSendTime(-1, 20))
        assertFalse(useCase.setDailyDigestSendTime(1, -20))
        assertFalse(useCase.setDailyDigestSendTime(24, 30))
    }
}