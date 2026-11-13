package com.example.smartexpensetracker.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.database.DatabaseHelper
import com.example.smartexpensetracker.utils.AlarmScheduler
import com.example.smartexpensetracker.utils.NotificationHelper
import com.example.smartexpensetracker.utils.ReminderPreferences
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BalanceEntryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var alarmScheduler: AlarmScheduler
    private lateinit var reminderPreferences: ReminderPreferences

    private lateinit var tilBalance: TextInputLayout
    private lateinit var etBalanceInput: TextInputEditText
    private lateinit var btnSaveBalance: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_balance_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        notificationHelper = NotificationHelper(this)
        alarmScheduler = AlarmScheduler(this)
        reminderPreferences = ReminderPreferences(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        tilBalance = findViewById(R.id.tilBalance)
        etBalanceInput = findViewById(R.id.etBalanceInput)
        btnSaveBalance = findViewById(R.id.btnSaveBalance)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupListeners() {
        btnSaveBalance.setOnClickListener {
            saveBalance()
        }
    }

    private fun saveBalance() {
        val inputStr = etBalanceInput.text.toString().trim()
        if (inputStr.isEmpty()) {
            tilBalance.error = getString(R.string.error_empty_balance)
            return
        }

        val balanceValue = try {
            inputStr.toDouble()
        } catch (_: NumberFormatException) {
            tilBalance.error = getString(R.string.error_invalid_number)
            return
        }

        if (balanceValue < 0) {
            tilBalance.error = getString(R.string.error_negative_balance)
            return
        }

        tilBalance.error = null

        // Get current date and time
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date(currentTimeMillis))
        val currentTime = timeFormat.format(Date(currentTimeMillis))

        // 1. Stop current reminder notification
        notificationHelper.cancelReminderNotification()

        // 2. Save balance in SQLite
        dbHelper.insertBalance(balanceValue, currentDate, currentTime, currentTimeMillis)

        // 3. Automatically schedule next daily reminder if enabled
        if (reminderPreferences.isReminderEnabled) {
            alarmScheduler.scheduleDailyAlarm(reminderPreferences.reminderHour, reminderPreferences.reminderMinute)
        }

        Toast.makeText(this, R.string.success_balance_saved, Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
