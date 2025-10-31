package org.hesab.app

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
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

        // Display the transaction data
        holder.tvDate.text = t.date
        holder.tvCategory.text = t.category
        holder.tvDescription.text = t.description

        // Formatting amount with thousands separator
        val df = DecimalFormat("#,###")
        holder.tvAmount.text = df.format(t.amount)

        // Set amount color based on income/expense
        val ctx = holder.itemView.context
        val color = if (t.isIncome) ctx.getColor(R.color.income_green) else ctx.getColor(R.color.expense_red)
        holder.tvAmount.setTextColor(color)

        // Set up the menu
        holder.ivMenu.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        // Handle edit action
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
                    R.id.menu_delete -> {
                        // Handle delete action
                        AlertDialog.Builder(v.context)
                            .setTitle("حذف تراکنش")
                            .setMessage("آیا از حذف این تراکنش مطمئن هستید؟")
                            .setPositiveButton("بله") { _, _ ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    db.transactionDao().delete(t)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        transactions.removeAt(position)
                                        notifyItemRemoved(position)
                                        Toast.makeText(v.context, "تراکنش حذف شد", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .setNegativeButton("خیر", null)
                            .show()
                        true
                    }
                    R.id.menu_move -> {
                        // Handle move action (swap positions)
                        Toast.makeText(v.context, "حالت جابجایی فعال است. برای خروج دوبار کلیک روی لیست یا دکمه بازگشت.", Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    // Function to move items (swap positions)
    fun moveItem(from: Int, to: Int) {
        if (from < 0 || to < 0 || from >= transactions.size || to >= transactions.size) return
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)

        // Update orderIndex in DB (background operation)
        CoroutineScope(Dispatchers.IO).launch {
            transactions.forEachIndexed { idx, tr ->
                val updated = tr.copy(orderIndex = idx)
                db.transactionDao().update(updated)
            }
        }
    }
}
