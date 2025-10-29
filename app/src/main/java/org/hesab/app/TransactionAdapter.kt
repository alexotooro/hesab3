package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var items: List<Transaction> = listOf()

    fun setData(data: List<Transaction>) {
        items = data
        notifyDataSetChanged()
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtType: TextView = itemView.findViewById(R.id.txtType)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        holder.txtType.text = if (item.type == "income") "درآمد" else "هزینه"
        holder.txtAmount.text = item.amount.toString()
        holder.txtDate.text = item.date
        holder.txtCategory.text = item.category
    }

    override fun getItemCount(): Int = items.size
}
