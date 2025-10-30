package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<Transaction> = emptyList()

    fun setData(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val btnMore: TextView = itemView.findViewById(R.id.btnMore)

        fun bind(transaction: Transaction) {
            tvDate.text = transaction.date
            tvAmount.text = "%,.0f".format(transaction.amount)
            tvCategory.text = transaction.category
            tvDescription.text = transaction.description

            // رنگ مبلغ بر اساس نوع
            val amountColor = if (transaction.type == "درآمد")
                0xFF1B5E20.toInt() // سبز تیره
            else
                0xFFB71C1C.toInt() // قرمز تیره
            tvAmount.setTextColor(amountColor)

            // منوی سه‌نقطه
            btnMore.setOnClickListener {
                showPopupMenu(it, transaction)
            }
        }

        private fun showPopupMenu(view: View, transaction: Transaction) {
            val popup = PopupMenu(view.context, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_transaction_item, popup.menu)

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
}
