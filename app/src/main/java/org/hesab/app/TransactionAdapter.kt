package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionAdapter(
    private val db: AppDatabase,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val transactions = mutableListOf<Transaction>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val t = transactions[position]
        holder.tvDate.text = t.date
        holder.tvAmount.text = t.amount.toString()
        holder.tvCategory.text = t.category
        holder.tvNote.text = t.note ?: ""

        holder.itemView.setOnLongClickListener {
            showPopupMenu(holder.itemView, t)
            true
        }
    }

    private fun showPopupMenu(view: View, transaction: Transaction) {
        val popup = PopupMenu(view.context, view)
        MenuInflater(view.context).inflate(R.menu.transaction_item_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_edit -> {
                    // TODO: Add edit logic
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

    override fun getItemCount() = transactions.size

    fun submitList(list: List<Transaction>) {
        transactions.clear()
        transactions.addAll(list)
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)

        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().updateOrder(transactions)
        }
    }
}
