// app/src/main/java/org/hesab/app/TransactionAdapter.kt
package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionAdapter(private var transactions: MutableList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMenu: ImageButton = view.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]

        holder.txtDate.text = item.date
        holder.txtAmount.text = String.format("%,d", item.amount)
        holder.txtCategory.text = item.category
        holder.txtDescription.text = item.description

        // رنگ مبلغ براساس نوع تراکنش
        holder.txtAmount.setTextColor(
            if (item.isIncome) 0xFF007E33.toInt() else 0xFFB71C1C.toInt()
        )

        holder.btnMenu.setOnClickListener { v ->
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.transaction_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            val db = AppDatabase.getDatabase(v.context)
                            db.transactionDao().delete(item)
                            transactions.removeAt(position)
                            launch(Dispatchers.Main) { notifyItemRemoved(position) }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    fun updateData(newList: List<Transaction>) {
        transactions.clear()
        transactions.addAll(newList)
        notifyDataSetChanged()
    }
}
