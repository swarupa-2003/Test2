package com.example.smartexpensetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smartexpensetracker.R
import com.example.smartexpensetracker.model.BalanceRecord
import java.util.Locale

class BalanceAdapter(private val balanceList: List<BalanceRecord>) :
    RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>() {

    class BalanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBalance: TextView = itemView.findViewById(R.id.tvItemBalance)
        val tvDate: TextView = itemView.findViewById(R.id.tvItemDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvItemTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_balance, parent, false)
        return BalanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalanceViewHolder, position: Int) {
        val record = balanceList[position]
        holder.tvBalance.text = String.format(Locale.getDefault(), "$%.2f", record.balance)
        holder.tvDate.text = record.date
        holder.tvTime.text = record.time
    }

    override fun getItemCount(): Int = balanceList.size
}
