package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: MutableList<Transaction>,
    private val listener: OnTransactionMenuClickListener
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnTransactionMenuClickListener {
        fun onEditClick(transaction: Transaction)
        fun onDeleteClick(transaction: Transaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvDate.text = transaction.date
        holder.tvAmount.text = transaction.amount.toString()
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        holder.btnMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.inflate(R.menu.transaction_item_menu)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_edit -> {
                        listener.onEditClick(transaction)
                        true
                    }
                    R.id.menu_delete -> {
                        listener.onDeleteClick(transaction)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)
    }
}
