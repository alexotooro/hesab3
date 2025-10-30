package org.hesab.app

import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.hesab.app.databinding.ItemTransactionBinding
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<Transaction> = emptyList()

    fun setData(list: List<Transaction>) {
        transactions = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            // قالب‌بندی مبلغ با جداکننده هزارگان
            val formattedAmount = NumberFormat.getNumberInstance(Locale.US)
                .format(transaction.amount)

            binding.txtDate.text = transaction.date
            binding.txtAmount.text = "$formattedAmount"
            binding.txtCategory.text = transaction.category
            binding.txtDescription.text = transaction.description

            // رنگ مبلغ بر اساس نوع
            val colorRes = if (transaction.type == "درآمد")
                android.R.color.holo_green_dark
            else
                android.R.color.holo_red_dark

            binding.txtAmount.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )

            // دکمه سه‌نقطه برای منو
            binding.btnMore.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                MenuInflater(view.context).inflate(R.menu.menu_transaction_item, popup.menu)
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
}
