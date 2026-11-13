package com.example.smartexpensetracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smartexpensetracker.utils.AlarmScheduler
import com.example.smartexpensetracker.utils.ReminderPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val preferences = ReminderPreferences(context)
            if (preferences.isReminderEnabled) {
                val scheduler = AlarmScheduler(context)
                scheduler.scheduleDailyAlarm(preferences.reminderHour, preferences.reminderMinute)
            }
        }
    }
}
