package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    private val decimalFormat = DecimalFormat("#,###")

    fun attachTouchHelper(helper: ItemTouchHelper) {
        this.touchHelper = helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.txtDate.text = transaction.date
        holder.txtAmount.text = decimalFormat.format(transaction.amount)
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        holder.txtAmount.setTextColor(
            if (transaction.type == "expense")
                context.getColor(android.R.color.holo_red_dark)
            else
                context.getColor(android.R.color.holo_green_dark)
        )

        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            MenuInflater(context).inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        onEdit(transaction)
                        true
                    }
                    R.id.menu_delete -> {
                        onDelete(transaction)
                        true
                    }
                    R.id.menu_move -> {
                        // فعال‌سازی Drag & Drop با لمس دکمه جابجایی
                        touchHelper?.startDrag(holder)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun moveItem(from: Int, to: Int) {
        val moved = transactions.removeAt(from)
        transactions.add(to, moved)
        notifyItemMoved(from, to)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    }
}
