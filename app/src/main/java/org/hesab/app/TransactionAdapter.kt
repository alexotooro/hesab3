package org.hesab.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private var transactions: MutableList<Transaction>,
    private val db: AppDatabase
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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
        val item = transactions[position]

        holder.txtDate.text = item.date
        holder.txtAmount.text = item.amount.toString()
        holder.txtCategory.text = item.category
        holder.txtDescription.text = item.description

        holder.btnMore.setOnClickListener {
            showPopupMenu(it, item, position)
        }
    }

    override fun getItemCount(): Int = transactions.size

    private fun showPopupMenu(view: View, item: Transaction, position: Int) {
        val popup = PopupMenu(context, view)
        popup.inflate(R.menu.menu_transaction_item)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val intent = Intent(context, AddTransactionActivity::class.java)
                    intent.putExtra("edit_transaction_id", item.id)
                    context.startActivity(intent)
                    true
                }

                R.id.action_delete -> {
                    Thread {
                        db.transactionDao().delete(item)
                        (context as MainActivity).runOnUiThread {
                            transactions.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }.start()
                    true
                }

                R.id.action_move_up -> {
                    if (position > 0) {
                        swapItems(position, position - 1)
                    }
                    true
                }

                R.id.action_move_down -> {
                    if (position < transactions.size - 1) {
                        swapItems(position, position + 1)
                    }
                    true
                }

                else -> false
            }
        }
        popup.show()
    }

    private fun swapItems(from: Int, to: Int) {
        val temp = transactions[from]
        transactions[from] = transactions[to]
        transactions[to] = temp

        notifyItemMoved(from, to)

        // بروزرسانی ترتیب در دیتابیس
        Thread {
            for (i in transactions.indices) {
                db.transactionDao().updateOrder(transactions[i].id, transactions.size - i)
