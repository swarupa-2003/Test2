package com.example.smartexpensetracker.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.adapter.BalanceAdapter
import com.example.smartexpensetracker.database.DatabaseHelper

class BalanceHistoryActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvBalanceHistory: RecyclerView
    private lateinit var tvEmptyHistory: TextView
    private lateinit var balanceAdapter: BalanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_balance_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        initViews()
        loadHistory()
    }

    private fun initViews() {
        rvBalanceHistory = findViewById(R.id.rvBalanceHistory)
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory)

        rvBalanceHistory.layoutManager = LinearLayoutManager(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadHistory() {
        val balanceList = dbHelper.getAllBalances()
        if (balanceList.isNotEmpty()) {
            balanceAdapter = BalanceAdapter(balanceList)
            rvBalanceHistory.adapter = balanceAdapter
            rvBalanceHistory.visibility = View.VISIBLE
            tvEmptyHistory.visibility = View.GONE
        } else {
            rvBalanceHistory.visibility = View.GONE
            tvEmptyHistory.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
