package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit,
    private val onReorderRequested: () -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null

    fun attachTouchHelper(helper: ItemTouchHelper) {
        this.touchHelper = helper
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvNote: TextView = itemView.findViewById(R.id.tvNote)
        val btnMenu: TextView = itemView.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvDate.text = transaction.date
        holder.tvAmount.text = transaction.amount.toString()
        holder.tvCategory.text = transaction.category
        holder.tvNote.text = transaction.note

        holder.btnMenu.setOnClickListener {
            val popup = PopupMenu(context, it)
            MenuInflater(context).inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> onEdit(transaction)
                    R.id.action_delete -> onDelete(transaction)
                    R.id.action_reorder -> onReorderRequested()
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun moveItem(from: Int, to: Int) {
        Collections.swap(transactions, from, to)
        notifyItemMoved(from, to)
    }
}
