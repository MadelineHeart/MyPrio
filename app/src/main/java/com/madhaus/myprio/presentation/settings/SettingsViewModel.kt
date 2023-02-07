package com.madhaus.myprio.presentation.settings

import com.madhaus.myprio.domain.SettingsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsViewModel
@Inject constructor(private val settingsUseCase: SettingsUseCase) {

    fun getDailyDigestMinPrio() = settingsUseCase.getDailyDigestMinPriority()
    fun setDailyDigestMinPrio(newVal: Int): Boolean =
        settingsUseCase.setDailyDigestMinPriority(newVal)

    fun getDailyDigestSendTime(): String {
        val time = settingsUseCase.getDailyDigestSendTime()
        val minutes = String.format("%2s", "${time.second}").replace(' ', '0')
        return "${time.first}:$minutes"
    }
    fun setDailyDigestSendTime(hours: Int, minutes: Int) =
        settingsUseCase.setDailyDigestSendTime(hours, minutes)
}