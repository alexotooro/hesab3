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
    private val transactions: MutableList<Transaction>,
    private val listener: OnTransactionMenuClickListener
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnTransactionMenuClickListener {
        fun onEditClicked(transaction: Transaction)
        fun onDeleteClicked(transaction: Transaction)
    }

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txt_date)
        val txtAmount: TextView = view.findViewById(R.id.txt_amount)
        val txtReason: TextView = view.findViewById(R.id.txt_reason)
        val txtNote: TextView = view.findViewById(R.id.txt_note)
        val menuIcon: ImageView = view.findViewById(R.id.menu_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.txtDate.text = transaction.date
        holder.txtAmount.text = transaction.amount.toString()
        holder.txtReason.text = transaction.reason
        holder.txtNote.text = transaction.note

        holder.menuIcon.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.menuIcon)
            MenuInflater(holder.itemView.context).inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_edit -> listener.onEditClicked(transaction)
                    R.id.menu_delete -> listener.onDeleteClicked(transaction)
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size
}
