package org.hesab.app

import android.content.Context
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(
    private val context: Context,
    private var transactionList: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var dragEnabled = false
    private var touchHelper: ItemTouchHelper? = null

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val btnMore: TextView = itemView.findViewById(R.id.btnMore)
    }

    fun attachTouchHelper(helper: ItemTouchHelper) {
        this.touchHelper = helper
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]

        holder.txtDate.text = transaction.date
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        val formattedAmount =
            NumberFormat.getNumberInstance(Locale.US).format(transaction.amount)
        holder.txtAmount.text = formattedAmount

        if (transaction.type == "هزینه") {
            holder.txtAmount.setTextColor(0xFFE53935.toInt())
        } else {
            holder.txtAmount.setTextColor(0xFF388E3C.toInt())
        }

        holder.btnMore.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEdit(transaction)
                        true
                    }
                    R.id.action_delete -> {
                        onDelete(transaction)
                        true
                    }
                    R.id.action_move -> {
                        dragEnabled = true
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        holder.itemView.setOnLongClickListener {
            if (dragEnabled) {
                touchHelper?.startDrag(holder)
            }
            true
        }
    }

    override fun getItemCount(): Int = transactionList.size

    fun moveItem(from: Int, to: Int) {
        if (from < to) {
            for (i in from until to) Collections.swap(transactionList, i, i + 1)
        } else {
            for (i in from downTo to + 1) Collections.swap(transactionList, i, i - 1)
        }
        notifyItemMoved(from, to)
    }

    fun updateList(newList: MutableList<Transaction>) {
        transactionList = newList
        notifyDataSetChanged()
    }
}
