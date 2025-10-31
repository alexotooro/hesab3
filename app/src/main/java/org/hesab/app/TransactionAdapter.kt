package org.hesab.app

import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionAdapter(
    private var transactions: MutableList<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val menuButton: ImageView = itemView.findViewById(R.id.menuButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvDate.text = transaction.date
        holder.tvAmount.text = String.format("%,d ریال", transaction.amount)
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        // مقداردهی دکمه منو
        holder.menuButton.setOnClickListener { view ->
            showPopupMenu(view, holder.adapterPosition)
        }

        // کلیک روی ردیف -> باز کردن ویرایش (همان رفتن به AddTransactionActivity برای ویرایش)
        holder.itemView.setOnClickListener {
            openEdit(holder.itemView, transaction)
        }
    }

    private fun showPopupMenu(view: View, position: Int) {
        val context = view.context
        val popup = PopupMenu(context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.transaction_item_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_edit -> {
                    // ویرایش: باز کردن AddTransactionActivity با اطلاعات ردیف
                    openEdit(view, transactions[position])
                    true
                }
                R.id.action_move_up -> {
                    moveItem(position, -1)
                    true
                }
                R.id.action_move_down -> {
                    moveItem(position, 1)
                    true
                }
                R.id.action_delete -> {
                    deleteItem(position)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun openEdit(view: View, transaction: Transaction) {
        val ctx = view.context
        val intent = Intent(ctx, AddTransactionActivity::class.java).apply {
            putExtra("id", transaction.id)
            putExtra("date", transaction.date)
            putExtra("amount", transaction.amount.toString())
            putExtra("category", transaction.category)
            putExtra("description", transaction.description)
            putExtra("isIncome", transaction.isIncome)
        }
        ctx.startActivity(intent)
    }

    private fun moveItem(position: Int, direction: Int) {
        val newPosition = position + direction
        if (position < 0 || position >= transactions.size) return
        if (newPosition < 0 || newPosition >= transactions.size) return

        // جابجایی در لیست محلی
        val temp = transactions[position]
        transactions[position] = transactions[newPosition]
        transactions[newPosition] = temp

        notifyItemMoved(position, newPosition)

        // ذخیره ترتیب جدید در دیتابیس (orderIndex)
        CoroutineScope(Dispatchers.IO).launch {
            // استفاده از App.db (که در App.kt مقداردهی شده)
            transactions.forEachIndexed { idx, t ->
                t.orderIndex = idx
                App.db.transactionDao().update(t)
            }
        }
    }

    private fun deleteItem(position: Int) {
        if (position < 0 || position >= transactions.size) return
        val transaction = transactions[position]

        // حذف در UI
        transactions.removeAt(position)
        notifyItemRemoved(position)

        // حذف در DB
        CoroutineScope(Dispatchers.IO).launch {
            App.db.transactionDao().delete(transaction)
        }
    }

    // برای به‌روزرسانی داده‌ها از بیرون
    fun updateList(newList: MutableList<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
