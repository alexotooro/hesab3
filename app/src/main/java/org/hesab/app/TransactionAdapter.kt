package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.txtDate.text = transaction.date
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // فرمت عددی با ویرگول
        val formatter = DecimalFormat("#,###")
        holder.txtAmount.text = formatter.format(transaction.amount)

        // رنگ مبلغ: قرمز = هزینه ، سبز = درآمد
        if (transaction.type == "expense") {
            holder.txtAmount.setTextColor(0xFFE53935.toInt()) // قرمز
        } else {
            holder.txtAmount.setTextColor(0xFF43A047.toInt()) // سبز
        }

        // منوی سه‌نقطه
        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_transaction_item)
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit -> onEdit(transaction)
                    R.id.action_delete -> onDelete(transaction)
                }
                true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size
}
