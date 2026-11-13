package com.example.smartexpensetracker.model

data class BalanceRecord(
    val id: Long,
    val balance: Double,
    val date: String,
    val time: String,
    val timestamp: Long
)
