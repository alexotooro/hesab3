package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: MutableList<Transaction>,
    private val listener: OnTransactionInteractionListener
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnTransactionInteractionListener {
        fun onEdit(transaction: Transaction)
        fun onDelete(transaction: Transaction)
        fun onRequestMoveMode()
    }

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.textDate)
        val txtAmount: TextView = view.findViewById(R.id.textAmount)
        val txtCategory: TextView = view.findViewById(R.id.textCategory)
        val txtDescription: TextView = view.findViewById(R.id.textDescription)
        val menuIcon: ImageView = view.findViewById(R.id.menu_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val tx = transactions[position]
        holder.txtDate.text = tx.date
        holder.txtAmount.text = formatAmount(tx.amount)
        holder.txtCategory.text = tx.category
        holder.txtDescription.text = tx.description

        // color amount
        holder.txtAmount.setTextColor(if (tx.isIncome) holder.itemView.context.getColor(R.color.amount_income) else holder.itemView.context.getColor(R.color.amount_expense))

        holder.menuIcon.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.menuIcon)
            MenuInflater(holder.itemView.context).inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_edit -> listener.onEdit(tx)
                    R.id.menu_delete -> listener.onDelete(tx)
                    R.id.menu_move -> listener.onRequestMoveMode()
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun replaceAll(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }

    fun swap(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)
    }

    fun getList(): List<Transaction> = transactions

    private fun formatAmount(amount: Long): String {
        return android.icu.text.NumberFormat.getInstance().format(amount)
    }
}
