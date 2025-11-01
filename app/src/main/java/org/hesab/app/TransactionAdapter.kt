package org.hesab.app

import android.content.Intent
import android.view.LayoutInflater
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
import java.text.DecimalFormat

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val db: AppDatabase
) : RecyclerView.Adapter<TransactionAdapter.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenu: ImageView = view.findViewById(R.id.ivMenu)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val t = transactions[position]

        // نمایش داده‌ها
        holder.tvDate.text = t.date
        holder.tvCategory.text = t.category
        holder.tvDescription.text = t.description

        // قالب‌بندی مبلغ
        val df = DecimalFormat("#,###")
        holder.tvAmount.text = df.format(t.amount)

        // رنگ بر اساس درآمد/هزینه
        val ctx = holder.itemView.context
        val color = if (t.isIncome) ctx.getColor(R.color.income_green) else ctx.getColor(R.color.expense_red)
        holder.tvAmount.setTextColor(color)

        // منوی سه‌نقطه‌ای
        holder.ivMenu.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = Intent(v.context, AddTransactionActivity::class.java)
                        intent.putExtra("transaction_id", t.id)
                        intent.putExtra("date", t.date)
                        intent.putExtra("amount", t.amount.toString())
                        intent.putExtra("category", t.category)
                        intent.putExtra("description", t.description)
                        intent.putExtra("isIncome", t.isIncome)
                        v.context.startActivity(intent)
                        true
                    }

                    R.id.action_delete -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.transactionDao().delete(t)
                            CoroutineScope(Dispatchers.Main).launch {
                                transactions.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }
                        true
                    }

                    R.id.action_move -> {
                        // قابلیت جابجایی ردیف‌ها (فعلاً رزرو شده برای آینده)
                        true
                    }

                    R.id.action_settings -> {
                        val intent = Intent(v.context, SettingsActivity::class.java)
                        v.context.startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    // جابجایی آیتم‌ها
    fun moveItem(from: Int, to: Int) {
        if (from < 0 || to < 0 || from >= transactions.size || to >= transactions.size) return
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)

        // بروزرسانی orderIndex در دیتابیس
        CoroutineScope(Dispatchers.IO).launch {
            transactions.forEachIndexed { idx, tr ->
                val updated = tr.copy(orderIndex = idx)
                db.transactionDao().update(updated)
            }
        }
    }
}
