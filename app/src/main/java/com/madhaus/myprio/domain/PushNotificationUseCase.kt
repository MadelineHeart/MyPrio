package com.madhaus.myprio.domain

import com.madhaus.myprio.data.TimeUtils
import com.madhaus.myprio.data.repos.SettingsRepository
import com.madhaus.myprio.presentation.models.PresoNotification

interface PushNotificationUseCase {
    fun fetchDailyDigest(forTime: Long): List<PresoNotification>
    fun getTimeToNextDigest(currentTime: Long): Long
}

class PushNotificationUseCaseImpl(
    private val taskUseCase: TaskUseCase,
    private val settingsRepository: SettingsRepository
) : PushNotificationUseCase {
    override fun fetchDailyDigest(forTime: Long): List<PresoNotification> {
        return taskUseCase.fetchTaskList(forTime)
            .filter { it.getPriority(forTime) >= settingsRepository.getDigestMinimumPriority() }
            .sortedBy { it.getPriority(forTime) } // Highest prio sent last so it's at top of notif stack
            .map {
                PresoNotification(
                    it.id,
                    it.title,
                    it.getPriority(forTime),
                    it.description
                )
            }
    }

    override fun getTimeToNextDigest(currentTime: Long): Long {
        return ((24 * 60 * 60 * 1000) -                          // An entire day
                TimeUtils.getMillisInCurrentDay(currentTime) +  // Time till 00:00 of the next day
                (1000 * 60 * settingsRepository.getDigestSendTimeInMinutes())) %// Send time into text day
                (24 * 60 * 60 * 1000) // Mod by entire day in case send time is later today
    }
}