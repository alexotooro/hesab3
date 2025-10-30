package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class TransactionAdapter(
    private val context: Context,
    private var transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit,
    private val onOrderChanged: (List<Transaction>) -> Unit,
    private val onMoveModeChanged: (Boolean) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var moveModeEnabled = false
    private var itemTouchHelper: ItemTouchHelper? = null

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtDate: TextView = itemView.findViewById(R.id.txtDate)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.txtDate.text = transaction.date
        holder.txtAmount.text = transaction.amount.toString()
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // منوی سه نقطه
        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            MenuInflater(context).inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEdit(transaction)
                        true
                    }

                    R.id.action_delete -> {
                        onDelete(transaction)
                        true
                    }

                    R.id.action_move -> {
                        toggleMoveMode()
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    private fun toggleMoveMode() {
        moveModeEnabled = !moveModeEnabled
        onMoveModeChanged(moveModeEnabled)
        if (moveModeEnabled) {
            Toast.makeText(
                context,
                "حالت جابجایی فعال است. برای خروج، روی لیست دابل‌کلیک کنید یا دکمه بازگشت را بزنید.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                if (!moveModeEnabled) return false
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                Collections.swap(transactions, fromPos, toPos)
                notifyItemMoved(fromPos, toPos)
                onOrderChanged(transactions)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)

        // دابل‌کلیک برای خروج از حالت جابجایی
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            private var lastClickTime = 0L
            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                if (event?.action == android.view.MotionEvent.ACTION_DOWN) {
                    val clickTime = System.currentTimeMillis()
                    if (clickTime - lastClickTime < 400 && moveModeEnabled) {
                        moveModeEnabled = false
                        onMoveModeChanged(false)
                        Toast.makeText(context, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
                    }
                    lastClickTime = clickTime
                }
                return false
            }
        })
    }

    fun disableMoveMode() {
        if (moveModeEnabled) {
            moveModeEnabled = false
            onMoveModeChanged(false)
            Toast.makeText(context, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
        }
    }
}
