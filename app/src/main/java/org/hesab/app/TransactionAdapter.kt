package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val db: AppDatabase
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.txtDate.text = transaction.date
        holder.txtAmount.text = transaction.amount.toString()
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent =
                            android.content.Intent(context, AddTransactionActivity::class.java)
                        intent.putExtra("edit_transaction_id", transaction.id)
                        context.startActivity(intent)
                        true
                    }

                    R.id.action_delete -> {
                        Thread {
                            db.transactionDao().delete(transaction)
                            (context as MainActivity).runOnUiThread {
                                transactions.removeAt(position)
                                notifyItemRemoved(position)
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

    // برای جابجایی با لمس
    fun onItemMove(fromPosition: Int, toPosition: Int) {
        Collections.swap(transactions, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun attachItemTouchHelperTo(recyclerView: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }
}
