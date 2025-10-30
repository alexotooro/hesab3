package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val db: AppDatabase,
    private val onListUpdated: () -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.tvAmount.text = transaction.amount.toString()
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        holder.btnMenu.setOnClickListener { view ->
            val popup = PopupMenu(view.context, view)
            popup.inflate(R.menu.menu_transaction_item)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        val intent = android.content.Intent(view.context, AddTransactionActivity::class.java)
                        intent.putExtra("transaction_id", transaction.id)
                        view.context.startActivity(intent)
                        true
                    }
                    R.id.action_delete -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            db.transactionDao().delete(transaction)
                            transactions.removeAt(holder.adapterPosition)
                            onListUpdated()
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    // 🟩 جابجایی آیتم‌ها (Drag & Drop)
    fun attachItemTouchHelper(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition

                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        Collections.swap(transactions, i, i + 1)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        Collections.swap(transactions, i, i - 1)
                    }
                }

                notifyItemMoved(fromPosition, toPosition)

                CoroutineScope(Dispatchers.IO).launch {
                    for ((index, t) in transactions.withIndex()) {
                        db.transactionDao().updateOrder(t.id, index)
                    }
                }

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // هیچ کاری نکن
            }

            override fun isLongPressDragEnabled(): Boolean = true
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
