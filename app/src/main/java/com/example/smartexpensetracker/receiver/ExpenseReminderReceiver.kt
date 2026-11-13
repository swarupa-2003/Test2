package com.example.smartexpensetracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smartexpensetracker.utils.AlarmScheduler
import com.example.smartexpensetracker.utils.NotificationHelper
import com.example.smartexpensetracker.utils.ReminderPreferences

class ExpenseReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Show notification
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showReminderNotification()

        // 2. Schedule next daily alarm if reminder is still enabled
        val preferences = ReminderPreferences(context)
        if (preferences.isReminderEnabled) {
            val scheduler = AlarmScheduler(context)
            scheduler.scheduleDailyAlarm(preferences.reminderHour, preferences.reminderMinute)
        }
    }
}
