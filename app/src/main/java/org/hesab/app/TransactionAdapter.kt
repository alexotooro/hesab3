package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvDate.text = transaction.date
        holder.tvAmount.text = "%,d".format(transaction.amount)
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        holder.tvAmount.setTextColor(
            if (transaction.isIncome)
                holder.itemView.context.getColor(R.color.green_income)
            else
                holder.itemView.context.getColor(R.color.red_expense)
        )

        holder.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> onEditClick(transaction)
                    R.id.action_delete -> onDeleteClick(transaction)
                }
                true
            }
            popup.show()
        }
    }
}
