package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val t = transactions[position]
        holder.tvDate.text = t.date
        holder.tvAmount.text = "%,d".format(t.amount)
        holder.tvCategory.text = t.category
        holder.tvDescription.text = t.description

        holder.itemView.setOnLongClickListener { v ->
            showPopupMenu(v.context, v, t)
            true
        }
    }

    private fun showPopupMenu(context: Context, anchor: View, transaction: Transaction) {
        val popup = PopupMenu(context, anchor)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.transaction_item_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_delete -> {
                    onDelete(transaction)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount(): Int = transactions.size

    fun updateData(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }

    fun moveItem(from: Int, to: Int) {
        val mutableList = transactions.toMutableList()
        val moved = mutableList.removeAt(from)
        mutableList.add(to, moved)
        transactions = mutableList
        notifyItemMoved(from, to)
    }
}
