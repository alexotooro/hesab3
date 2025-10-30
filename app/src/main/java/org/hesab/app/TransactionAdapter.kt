package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateText: TextView = itemView.findViewById(R.id.textDate)
        val amountText: TextView = itemView.findViewById(R.id.textAmount)
        val purposeText: TextView = itemView.findViewById(R.id.textPurpose)
        val descriptionText: TextView = itemView.findViewById(R.id.textDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.dateText.text = transaction.date
        holder.amountText.text = transaction.amount.toString()
        holder.purposeText.text = transaction.purpose
        holder.descriptionText.text = transaction.description
    }

    override fun getItemCount(): Int = transactions.size

    fun setData(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
