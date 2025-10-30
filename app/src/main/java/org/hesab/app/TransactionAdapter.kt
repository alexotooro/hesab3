package org.hesab.app

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit,
    private val onOrderChanged: (List<Transaction>) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var moveMode = false
    private var touchHelper: androidx.recyclerview.widget.ItemTouchHelper? = null

    fun isMoveMode() = moveMode
    fun setMoveMode(enabled: Boolean) {
        moveMode = enabled
        notifyDataSetChanged()
    }

    fun attachTouchHelper(helper: androidx.recyclerview.widget.ItemTouchHelper) {
        this.touchHelper = helper
    }

    fun moveItem(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)
        onOrderChanged(transactions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val t = transactions[position]
        holder.txtDate.text = t.date
        holder.txtCategory.text = t.category
        holder.txtDescription.text = t.description
        holder.txtAmount.text = "%,d".format(t.amount)

        // رنگ مبلغ بر اساس نوع تراکنش
        holder.txtAmount.setTextColor(
            if (t.amount < 0) 0xFFD32F2F.toInt() else 0xFF388E3C.toInt()
        )

        holder.btnMore.setOnClickListener { v ->
            val popup = PopupMenu(context, v)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

            popup.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> onEdit(t)
                    R.id.action_delete -> onDelete(t)
                    R.id.action_move -> {
                        moveMode = true
                        (context as MainActivity).showMoveModeBanner(true)
                    }
                }
                true
            }
            popup.show()
        }

        holder.itemView.alpha = if (moveMode) 0.8f else 1f
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }
}
