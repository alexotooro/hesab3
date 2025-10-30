package org.hesab.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private var transactions: List<Transaction>,
    private val db: AppDatabase
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: TextView = view.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.txtDate.text = transaction.date
        holder.txtAmount.text = String.format("%,.0f", transaction.amount)
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // رنگ مبلغ بر اساس نوع
        holder.txtAmount.setTextColor(
            if (transaction.type == "درآمد") 0xFF2E7D32.toInt() else 0xFFD32F2F.toInt()
        )

        // دکمه سه‌نقطه
        holder.btnMore.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(context, AddTransactionActivity::class.java)
                        intent.putExtra("edit_transaction_id", transaction.id)
                        context.startActivity(intent)
                        true
                    }
                    R.id.action_delete -> {
                        Thread {
                            db.transactionDao().delete(transaction)
                            (context as MainActivity).runOnUiThread {
                                (context as MainActivity).refreshTransactions()
                            }
                        }.start()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    fun updateData(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
