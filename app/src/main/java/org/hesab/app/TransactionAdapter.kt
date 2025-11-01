package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val menuButton: ImageView = itemView.findViewById(R.id.menuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvDate.text = transaction.date.toString().substring(0, 10)
        holder.tvAmount.text = transaction.amount.toString()
        holder.tvCategory.text = transaction.category
        holder.tvNote.text = transaction.note ?: ""

        // منوی سه‌نقطه
        holder.menuButton.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.transaction_item_menu, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEdit(transaction)
                        true
                    }
                    R.id.action_delete -> {
                        onDelete(transaction)
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

    fun removeItem(position: Int) {
        transactions.removeAt(position)
        notifyItemRemoved(position)
    }

    fun moveItem(from: Int, to: Int) {
        val movedItem = transactions.removeAt(from)
        transactions.add(to, movedItem)
        notifyItemMoved(from, to)
    }
}
