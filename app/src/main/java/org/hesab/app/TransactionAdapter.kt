package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val db: AppDatabase,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var dragEnabled = false
    private var itemTouchHelper: ItemTouchHelper? = null

    init {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                Collections.swap(transactions, fromPos, toPos)
                notifyItemMoved(fromPos, toPos)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun isLongPressDragEnabled() = dragEnabled
        }
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]

        holder.txtDate.text = transaction.date
        holder.txtAmount.text = transaction.amount.toString()
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        holder.btnMore.setOnClickListener {
            val popup = PopupMenu(context, holder.btnMore)
            popup.menuInflater.inflate(R.menu.transaction_item_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> {
                        (context as MainActivity).startEditTransaction(transaction)
                        true
                    }
                    R.id.menu_delete -> {
                        Thread {
                            db.transactionDao().delete(transaction)
                            (context as MainActivity).runOnUiThread {
                                transactions.removeAt(position)
                                notifyItemRemoved(position)
                                context.updateBalance()
                            }
                        }.start()
                        true
                    }
                    R.id.menu_move -> {
                        dragEnabled = true
                        (context as MainActivity).showToast("حالت جابجایی فعال شد. ردیف را بکشید.")
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = transactions.size

    fun disableDrag() {
        dragEnabled = false
    }
}
