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
    }

    @Test
    fun getDailyDigestSendTime() {
        val expected = (SettingsRepositoryMock.DIGEST_SEND_TIME / 60) to (SettingsRepositoryMock.DIGEST_SEND_TIME % 60)
        assertEquals(useCase.getDailyDigestSendTime(), expected)
    }

    @Test
    fun setDailyDigestSendTime() {
    }
}