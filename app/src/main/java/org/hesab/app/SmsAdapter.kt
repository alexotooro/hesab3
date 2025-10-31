package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SmsAdapter(private val smsList: List<Transaction>) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    class SmsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvSmsDate)
        val tvAmount: TextView = view.findViewById(R.id.tvSmsAmount)
        val tvType: TextView = view.findViewById(R.id.tvSmsType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val sms = smsList[position]
        holder.tvDate.text = sms.date
        holder.tvAmount.text = "${sms.amount} ریال"
        holder.tvType.text = if (sms.isIncome) "واریز" else "برداشت"
    }

    override fun getItemCount(): Int = smsList.size
}
