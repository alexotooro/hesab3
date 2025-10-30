package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val db: AppDatabase,
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit,
    private val onListUpdated: () -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
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

    override fun getItemCount() = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.tvDate.text = transaction.date
        holder.tvAmount.text = "%,d".format(transaction.amount)
        holder.tvCategory.text = transaction.category
        holder.tvDescription.text = transaction.description

        holder.tvAmount.setTextColor(
            if (transaction.isIncome)
                holder.itemView.context.getColor(R.color.green_income)
            else
                holder.itemView.context.getColor(R.color.red_expense)
        )

        holder.btnMenu.setOnClickListener {
            val popup = android.widget.PopupMenu(holder.itemView.context, holder.btnMenu)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(transaction)
                    }

                    R.id.action_delete -> {
                        onDeleteClick(transaction)
                    }

                    R.id.action_move -> {
                        showMoveMenu(holder, transaction)
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun showMoveMenu(holder: ViewHolder, transaction: Transaction) {
        val moveMenu = android.widget.PopupMenu(holder.itemView.context, holder.btnMenu)
        moveMenu.menu.add("انتقال به بالا")
        moveMenu.menu.add("انتقال به پایین")

        moveMenu.setOnMenuItemClickListener { moveItem ->
            val currentIndex = transactions.indexOf(transaction)
            when (moveItem.title) {
                "انتقال به بالا" -> {
                    if (currentIndex > 0) {
                        swapOrder(currentIndex, currentIndex - 1)
                    } else {
                        Toast.makeText(holder.itemView.context, "در بالاترین موقعیت است", Toast.LENGTH_SHORT).show()
                    }
                }
                "انتقال به پایین" -> {
                    if (currentIndex < transactions.size - 1) {
                        swapOrder(currentIndex, currentIndex + 1)
                    } else {
                        Toast.makeText(holder.itemView.context, "در پایین‌ترین موقعیت است", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        }

        moveMenu.show()
    }

    private fun swapOrder(fromIndex: Int, toIndex: Int) {
        val t1 = transactions[fromIndex]
        val t2 = transactions[toIndex]
        val tempOrder = t1.orderIndex
        t1.orderIndex = t2.orderIndex
        t2.orderIndex = tempOrder

        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().update(t1)
            db.transactionDao().update(t2)
        }

        transactions[fromIndex] = t2
        transactions[toIndex] = t1

        notifyItemMoved(fromIndex, toIndex)
        onListUpdated()
    }
}
