package com.example.smartexpensetracker.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.smartexpensetracker.model.BalanceRecord

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SmartExpenseTracker.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_BALANCE_HISTORY = "balance_history"
        private const val COLUMN_ID = "id"
        private const val COLUMN_BALANCE = "balance"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE " + TABLE_BALANCE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BALANCE + " REAL NOT NULL, "
                + COLUMN_DATE + " TEXT NOT NULL, "
                + COLUMN_TIME + " TEXT NOT NULL, "
                + COLUMN_TIMESTAMP + " INTEGER NOT NULL" + ")")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BALANCE_HISTORY")
        onCreate(db)
    }

    fun insertBalance(balance: Double, date: String, time: String, timestamp: Long): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BALANCE, balance)
            put(COLUMN_DATE, date)
            put(COLUMN_TIME, time)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        val id = db.insert(TABLE_BALANCE_HISTORY, null, values)
        db.close()
        return id
    }

    fun getAllBalances(): List<BalanceRecord> {
        val balanceList = mutableListOf<BalanceRecord>()
        val selectQuery = "SELECT * FROM $TABLE_BALANCE_HISTORY ORDER BY $COLUMN_TIMESTAMP DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val balanceIndex = it.getColumnIndex(COLUMN_BALANCE)
                val dateIndex = it.getColumnIndex(COLUMN_DATE)
                val timeIndex = it.getColumnIndex(COLUMN_TIME)
                val timestampIndex = it.getColumnIndex(COLUMN_TIMESTAMP)

                do {
                    val id = if (idIndex != -1) it.getLong(idIndex) else 0L
                    val balance = if (balanceIndex != -1) it.getDouble(balanceIndex) else 0.0
                    val date = if (dateIndex != -1) it.getString(dateIndex) else ""
                    val time = if (timeIndex != -1) it.getString(timeIndex) else ""
                    val timestamp = if (timestampIndex != -1) it.getLong(timestampIndex) else 0L

                    balanceList.add(BalanceRecord(id, balance, date, time, timestamp))
                } while (it.moveToNext())
            }
        }
        db.close()
        return balanceList
    }

    fun getLatestBalance(): BalanceRecord? {
        var record: BalanceRecord? = null
        val selectQuery = "SELECT * FROM $TABLE_BALANCE_HISTORY ORDER BY $COLUMN_TIMESTAMP DESC LIMIT 1"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndex(COLUMN_ID)
                val balanceIndex = it.getColumnIndex(COLUMN_BALANCE)
                val dateIndex = it.getColumnIndex(COLUMN_DATE)
                val timeIndex = it.getColumnIndex(COLUMN_TIME)
                val timestampIndex = it.getColumnIndex(COLUMN_TIMESTAMP)

                val id = if (idIndex != -1) it.getLong(idIndex) else 0L
                val balance = if (balanceIndex != -1) it.getDouble(balanceIndex) else 0.0
                val date = if (dateIndex != -1) it.getString(dateIndex) else ""
                val time = if (timeIndex != -1) it.getString(timeIndex) else ""
                val timestamp = if (timestampIndex != -1) it.getLong(timestampIndex) else 0L

                record = BalanceRecord(id, balance, date, time, timestamp)
            }
        }
        db.close()
        return record
    }

    fun getBalanceCount(): Int {
        val countQuery = "SELECT COUNT(*) FROM $TABLE_BALANCE_HISTORY"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        var count = 0
        cursor.use {
            if (it.moveToFirst()) {
                count = it.getInt(0)
            }
        }
        db.close()
        return count
    }
}
