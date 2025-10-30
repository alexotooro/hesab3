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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    private var moveMode = false

    fun attachTouchHelper(helper: ItemTouchHelper) {
        touchHelper = helper
    }

    fun moveItem(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)
    }

    fun isMoveMode() = moveMode

    fun setMoveMode(enabled: Boolean) {
        moveMode = enabled
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactions[position]
        holder.txtDate.text = item.date
        holder.txtAmount.text = item.amount.toString()
        holder.txtCategory.text = item.category
        holder.txtDescription.text = item.description

        holder.btnMore.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            MenuInflater(context).inflate(R.menu.menu_transaction_item, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.action_edit -> onEdit(item)
                    R.id.action_delete -> onDelete(item)
                    R.id.action_move -> {
                        moveMode = true
                    }
                }
                true
            }
            popup.show()
        }

        holder.itemView.setOnLongClickListener {
            if (moveMode) {
                touchHelper?.startDrag(holder)
                true
            } else false
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }
}
