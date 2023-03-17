package com.madhaus.myprio.mocks.data

import com.madhaus.myprio.data.repos.SettingsRepository
import org.mockito.Mockito
import org.mockito.kotlin.whenever

object SettingsRepositoryMock {
    const val MINIMUM_PRIORITY = 3
    const val DIGEST_SEND_TIME = 480 // 8:00 am

    fun getStandardMockedRepo(): SettingsRepository {
        val repo: SettingsRepository = Mockito.mock(SettingsRepository::class.java)
        whenever(repo.getDigestMinimumPriority()).thenReturn(MINIMUM_PRIORITY)
        whenever(repo.getDigestSendTimeInMinutes()).thenReturn(DIGEST_SEND_TIME)
        return repo
    }
}