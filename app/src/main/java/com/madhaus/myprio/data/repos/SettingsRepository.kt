package com.madhaus.myprio.data.repos

import android.content.Context
import android.content.SharedPreferences

interface SettingsRepository {
    fun getDigestMinimumPriority(): Int
    fun setDigestMinimumPriority(newVal: Int)

    fun getDigestSendTimeInMinutes(): Int
    fun setDigestSendTimeInMinutes(newVal: Int)
}

class SettingsRepositoryImpl(private val context: Context): SettingsRepository {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    override fun getDigestMinimumPriority(): Int =
        sharedPreferences.getInt(DIGEST_MINIMUM_PRIORITY_KEY, DIGEST_MINIMUM_PRIORITY_DEFAULT)

    override fun setDigestMinimumPriority(newVal: Int) =
        sharedPreferences.edit().putInt(DIGEST_MINIMUM_PRIORITY_KEY, newVal).apply()

    override fun getDigestSendTimeInMinutes(): Int =
        sharedPreferences.getInt(DIGEST_SEND_TIME_KEY, DIGEST_SEND_TIME_DEFAULT)

    override fun setDigestSendTimeInMinutes(newVal: Int) =
        sharedPreferences.edit().putInt(DIGEST_SEND_TIME_KEY, newVal).apply()

    private companion object {
        private const val SHARED_PREF_NAME = "my_prio_shared_preferences"

        private const val DIGEST_MINIMUM_PRIORITY_KEY = "digest_minimum_priority"
        private const val DIGEST_MINIMUM_PRIORITY_DEFAULT = 1

        private const val DIGEST_SEND_TIME_KEY = "digest_send_time_key"
        private const val DIGEST_SEND_TIME_DEFAULT = 8 * 60 // 8:00 am
    }
}