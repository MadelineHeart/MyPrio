package com.madhaus.myprio.domain

import com.madhaus.myprio.data.repos.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor

@Singleton
class SettingsUseCase
@Inject constructor(private val settingsRepo: SettingsRepository) {

    fun getDailyDigestMinPriority(): Int {
        return settingsRepo.getDigestMinimumPriority()
    }

    fun setDailyDigestMinPriority(newVal: Int): Boolean {
        if (!(0..9).contains(newVal))
            return false
        settingsRepo.setDigestMinimumPriority(newVal)
        return true
    }

    fun getDailyDigestSendTime(): Pair<Int, Int> {
        val sumMins = settingsRepo.getDigestSendTimeInMinutes()
        return (sumMins / 60) to (sumMins % 60)
    }

    fun setDailyDigestSendTime(hours: Int, minutes: Int): Boolean {
        if (!(0..24).contains(hours) && !(0..60).contains(minutes))
            return false
        settingsRepo.setDigestSendTimeInMinutes((hours * 60) + minutes)
        return true
    }
}