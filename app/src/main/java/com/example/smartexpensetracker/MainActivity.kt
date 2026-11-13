package com.example.smartexpensetracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smartexpensetracker.activity.BalanceEntryActivity
import com.example.smartexpensetracker.activity.BalanceHistoryActivity
import com.example.smartexpensetracker.activity.ReminderSettingsActivity
import com.example.smartexpensetracker.database.DatabaseHelper
import com.example.smartexpensetracker.utils.ReminderPreferences
import com.google.android.material.button.MaterialButton
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var reminderPreferences: ReminderPreferences

    private lateinit var tvCurrentBalance: TextView
    private lateinit var tvLastUpdate: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var tvNextReminder: TextView
    private lateinit var btnUpdateBalance: MaterialButton
    private lateinit var btnViewHistory: MaterialButton
    private lateinit var btnReminderSettings: MaterialButton

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        reminderPreferences = ReminderPreferences(this)

        initViews()
        setupListeners()
        checkAndRequestPermissions()
    }

    override fun onResume() {
        super.onResume()
        updateDashboard()
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun initViews() {
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance)
        tvLastUpdate = findViewById(R.id.tvLastUpdate)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        tvNextReminder = findViewById(R.id.tvNextReminder)
        btnUpdateBalance = findViewById(R.id.btnUpdateBalance)
        btnViewHistory = findViewById(R.id.btnViewHistory)
        btnReminderSettings = findViewById(R.id.btnReminderSettings)
    }

    private fun setupListeners() {
        btnUpdateBalance.setOnClickListener {
            val intent = Intent(this, BalanceEntryActivity::class.java)
            startActivity(intent)
        }

        btnViewHistory.setOnClickListener {
            val intent = Intent(this, BalanceHistoryActivity::class.java)
            startActivity(intent)
        }

        btnReminderSettings.setOnClickListener {
            val intent = Intent(this, ReminderSettingsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateDashboard() {
        val latestRecord = dbHelper.getLatestBalance()
        if (latestRecord != null) {
            tvCurrentBalance.text = String.format(Locale.getDefault(), "$%.2f", latestRecord.balance)
            tvLastUpdate.text = getString(R.string.last_update_prefix) + "${latestRecord.date} at ${latestRecord.time}"
            tvCurrentBalance.visibility = View.VISIBLE
            tvLastUpdate.visibility = View.VISIBLE
            tvEmptyState.visibility = View.GONE
        } else {
            tvCurrentBalance.text = "$0.00"
            tvLastUpdate.visibility = View.GONE
            tvEmptyState.visibility = View.VISIBLE
        }

        // Update reminder status info
        if (reminderPreferences.isReminderEnabled) {
            val hour = reminderPreferences.reminderHour
            val minute = reminderPreferences.reminderMinute
            val amPm = if (hour >= 12) "PM" else "AM"
            val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
            tvNextReminder.text = String.format(Locale.getDefault(), "Scheduled daily at %02d:%02d %s", displayHour, minute, amPm)
        } else {
            tvNextReminder.text = "Status: Disabled"
        }
    }
}
