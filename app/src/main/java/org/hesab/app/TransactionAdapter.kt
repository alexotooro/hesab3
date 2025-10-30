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
import java.text.DecimalFormat

class TransactionAdapter(
    private val context: Context,
    private val transactions: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    var isMoveMode = false

    fun attachTouchHelper(helper: ItemTouchHelper) {
        touchHelper = helper
    }

    fun moveItem(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)
    }

    fun exitMoveMode() {
        isMoveMode = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val formatter = DecimalFormat("#,###")

        holder.txtDate.text = transaction.date
        holder.txtAmount.text = formatter.format(transaction.amount) + " ریال"
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // رنگ متفاوت برای درآمد/هزینه
        holder.txtAmount.setTextColor(
            if (transaction.type == "درآمد")
                0xFF00796B.toInt()
            else
                0xFFD32F2F.toInt()
        )

        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.menu_transaction_item, popup.menu)

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
                        isMoveMode = true
                        notifyDataSetChanged()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }
}
