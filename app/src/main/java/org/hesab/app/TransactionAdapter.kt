package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.hesab.app.db.Transaction

class TransactionAdapter(
    private var transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
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
        holder.tvNote.text = transaction.note

        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, it)
            MenuInflater(holder.itemView.context)
                .inflate(R.menu.transaction_item_menu, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_edit -> onEdit(transaction)
                    R.id.menu_delete -> onDelete(transaction)
                }
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }
}
