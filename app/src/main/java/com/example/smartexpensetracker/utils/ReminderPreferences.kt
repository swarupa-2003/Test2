package com.example.smartexpensetracker.utils

import android.content.Context
import android.content.SharedPreferences

class ReminderPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "smart_expense_reminder_prefs"
        private const val KEY_IS_ENABLED = "is_reminder_enabled"
        private const val KEY_HOUR = "reminder_hour"
        private const val KEY_MINUTE = "reminder_minute"
    }

    var isReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_IS_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_ENABLED, value).apply()

    var reminderHour: Int
        get() = prefs.getInt(KEY_HOUR, 9) // Default 9 AM
        set(value) = prefs.edit().putInt(KEY_HOUR, value).apply()

    var reminderMinute: Int
        get() = prefs.getInt(KEY_MINUTE, 0)
        set(value) = prefs.edit().putInt(KEY_MINUTE, value).apply()

    fun saveReminderTime(hour: Int, minute: Int, enabled: Boolean) {
        prefs.edit().apply {
            putInt(KEY_HOUR, hour)
            putInt(KEY_MINUTE, minute)
            putBoolean(KEY_IS_ENABLED, enabled)
            apply()
        }
    }
}
