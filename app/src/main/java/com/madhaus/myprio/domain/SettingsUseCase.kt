package com.madhaus.myprio.domain

import com.madhaus.myprio.data.repos.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.floor

interface SettingsUseCase {
    fun getDailyDigestMinPriority(): Int
    fun setDailyDigestMinPriority(newVal: Int): Boolean

    fun getDailyDigestSendTime(): Pair<Int, Int>
    fun setDailyDigestSendTime(hours: Int, minutes: Int): Boolean
}

class SettingsUseCaseImpl(private val settingsRepo: SettingsRepository) : SettingsUseCase {

    override fun getDailyDigestMinPriority(): Int {
        return settingsRepo.getDigestMinimumPriority()
    }

    override fun setDailyDigestMinPriority(newVal: Int): Boolean {
        if (!(1..9).contains(newVal))
            return false
        settingsRepo.setDigestMinimumPriority(newVal)
        return true
    }

    override fun getDailyDigestSendTime(): Pair<Int, Int> {
        val sumMins = settingsRepo.getDigestSendTimeInMinutes()
        return (sumMins / 60) to (sumMins % 60)
    }

    override fun setDailyDigestSendTime(hours: Int, minutes: Int): Boolean {
        val toSetMinutes = (hours * 60) + minutes
        if (!(0..24).contains(hours) ||
            !(0..60).contains(minutes) ||
            !(0..1440).contains(toSetMinutes))
            return false
        settingsRepo.setDigestSendTimeInMinutes(toSetMinutes)
        return true
    }
}