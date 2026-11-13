package com.example.smartexpensetracker.activity

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.utils.AlarmScheduler
import com.example.smartexpensetracker.utils.ReminderPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class ReminderSettingsActivity : AppCompatActivity() {

    private lateinit var reminderPreferences: ReminderPreferences
    private lateinit var alarmScheduler: AlarmScheduler

    private lateinit var switchReminder: SwitchMaterial
    private lateinit var timePicker: TimePicker
    private lateinit var btnSaveReminder: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reminder_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        reminderPreferences = ReminderPreferences(this)
        alarmScheduler = AlarmScheduler(this)

        initViews()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        switchReminder = findViewById(R.id.switchReminder)
        timePicker = findViewById(R.id.timePicker)
        btnSaveReminder = findViewById(R.id.btnSaveReminder)

        timePicker.setIs24HourView(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadSettings() {
        val isEnabled = reminderPreferences.isReminderEnabled
        val hour = reminderPreferences.reminderHour
        val minute = reminderPreferences.reminderMinute

        switchReminder.isChecked = isEnabled
        timePicker.hour = hour
        timePicker.minute = minute
    }

    private fun setupListeners() {
        btnSaveReminder.setOnClickListener {
            val isEnabled = switchReminder.isChecked
            val hour = timePicker.hour
            val minute = timePicker.minute

            // Save preferences
            reminderPreferences.saveReminderTime(hour, minute, isEnabled)

            // Handle AlarmManager scheduling
            if (isEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = getSystemService(AlarmManager::class.java)
                    if (!alarmManager.canScheduleExactAlarms()) {
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                        Toast.makeText(this, "Please grant exact alarm permission for reminders", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
                alarmScheduler.scheduleDailyAlarm(hour, minute)
                Toast.makeText(this, getString(R.string.success_reminder_saved), Toast.LENGTH_SHORT).show()
            } else {
                alarmScheduler.cancelAlarm()
                Toast.makeText(this, "Daily reminders disabled", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
