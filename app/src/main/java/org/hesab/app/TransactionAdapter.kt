// TransactionAdapter.kt
package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // تنظیم مقادیر
        holder.tvAmount.text = "%,d".format(transaction.amount)
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description ?: ""

        // رنگ متن بر اساس نوع تراکنش (درآمد/هزینه)
        val colorRes = if (transaction.type == "income")
            android.R.color.holo_green_dark
        else
            android.R.color.holo_red_dark
        holder.tvAmount.setTextColor(holder.itemView.context.getColor(colorRes))

        // منوی سه‌نقطه
        holder.btnMenu.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> onEditClick(transaction)
                    R.id.action_delete -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            onDeleteClick(transaction)
                        }
                    }
                }
                true
            }

            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size
}
