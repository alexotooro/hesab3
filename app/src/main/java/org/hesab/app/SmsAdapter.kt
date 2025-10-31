package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SmsAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<SmsAdapter.SmsViewHolder>() {

    class SmsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvSmsDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvSmsAmount)
        val tvType: TextView = itemView.findViewById(R.id.tvSmsType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sms_transaction, parent, false)
        return SmsViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmsViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvDate.text = transaction.date
        holder.tvAmount.text = "%,d ریال".format(transaction.amount)
        holder.tvType.text = if (transaction.isIncome) "واریز" else "برداشت"
        holder.tvType.setTextColor(
            holder.itemView.context.getColor(
                if (transaction.isIncome) R.color.income_green else R.color.expense_red
            )
        )
    }

    override fun getItemCount(): Int = transactions.size
}
