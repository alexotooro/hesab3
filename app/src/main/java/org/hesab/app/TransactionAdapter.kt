package org.hesab.app

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private val transactions: List<Transaction>,
    private val db: AppDatabase
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
        holder.txtAmount.text = String.format("%,.0f", transaction.amount)
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // رنگ ردیف‌ها یکی در میان آبی خیلی کمرنگ و سفید برای خوانایی
        holder.itemView.setBackgroundColor(
            if (position % 2 == 0) android.graphics.Color.parseColor("#E7F1FF")
            else android.graphics.Color.parseColor("#FFFFFF")
        )

        // منو برای ویرایش یا حذف
        holder.btnMore.setOnClickListener {
            val options = arrayOf("ویرایش", "حذف")
            AlertDialog.Builder(context)
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> { // ویرایش
                            val intent = Intent(context, AddTransactionActivity::class.java)
                            intent.putExtra("edit_transaction_id", transaction.id)
                            context.startActivity(intent)
                        }
                        1 -> { // حذف
                            Thread {
                                db.transactionDao().delete(transaction)
                                if (context is MainActivity) {
                                    context.runOnUiThread { context.recreate() }
                                }
                            }.start()
                        }
                    }
                }.show()
        }
    }

    override fun getItemCount(): Int = transactions.size
}
