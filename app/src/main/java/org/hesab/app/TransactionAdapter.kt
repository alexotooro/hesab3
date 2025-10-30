package org.hesab.app

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import org.hesab.app.databinding.ItemTransactionBinding

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
            binding.txtDate.text = transaction.date
            binding.txtAmount.text = "%,.0f".format(transaction.amount)
            binding.txtCategory.text = transaction.category
            binding.txtDescription.text = transaction.description

            // رنگ درآمد و هزینه
            val colorRes = if (transaction.type == "درآمد")
                android.R.color.holo_green_dark
            else
                android.R.color.holo_red_dark

            binding.txtAmount.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )

            // منوی سه‌نقطه
            binding.btnMore.setOnClickListener {
                val popup = PopupMenu(binding.root.context, it)
                popup.menu.add("ویرایش")
                popup.menu.add("حذف")

                popup.setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "ویرایش" -> onEdit(transaction)
                        "حذف" -> onDelete(transaction)
                    }
                    true
                }
                popup.show()
            }
        }
    }
}
