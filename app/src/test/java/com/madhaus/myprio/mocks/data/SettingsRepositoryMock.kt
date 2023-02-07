package com.madhaus.myprio.mocks.data

import com.madhaus.myprio.data.repos.SettingsRepository

class SettingsRepositoryMock: SettingsRepository {
    override fun getDigestMinimumPriority(): Int {
        TODO("Not yet implemented")
    }

    override fun setDigestMinimumPriority(newVal: Int) {
        TODO("Not yet implemented")
    }

    override fun getDigestSendTimeInMinutes(): Int {
        TODO("Not yet implemented")
    }

    override fun setDigestSendTimeInMinutes(newVal: Int) {
        TODO("Not yet implemented")
    }
}