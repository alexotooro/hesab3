package org.hesab.app

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
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
    private val onDelete: (Transaction) -> Unit,
    private val onOrderChanged: (List<Transaction>) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var itemTouchHelper: ItemTouchHelper? = null
    private var moveMode = false

    fun attachTouchHelper(helper: ItemTouchHelper) {
        this.itemTouchHelper = helper
    }

    fun isMoveMode(): Boolean = moveMode

    fun setMoveMode(enabled: Boolean) {
        moveMode = enabled
    }

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
        holder.txtAmount.text = "%,d".format(transaction.amount)
        holder.txtCategory.text = transaction.category
        holder.txtDescription.text = transaction.description

        // 🎨 رنگ مبلغ بر اساس نوع
        when (transaction.type) {
            "درآمد" -> holder.txtAmount.setTextColor(Color.parseColor("#2E7D32")) // سبز
            "هزینه" -> holder.txtAmount.setTextColor(Color.parseColor("#C62828")) // قرمز
            else -> holder.txtAmount.setTextColor(Color.BLACK)
        }

        // 🎨 سایر فیلدها مشکی پررنگ
        val darkText = Color.parseColor("#000000")
        holder.txtDate.setTextColor(darkText)
        holder.txtCategory.setTextColor(darkText)
        holder.txtDescription.setTextColor(darkText)

        // 🎛 منوی سه‌نقطه
        holder.btnMore.setOnClickListener {
            val popup = PopupMenu(context, holder.btnMore)
            popup.menuInflater.inflate(R.menu.menu_transaction, popup.menu)

            popup.setOnMenuItemClickListener { item ->
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
                        moveMode = true
                        android.widget.Toast.makeText(
                            context,
                            "🔹 حالت جابجایی فعال شد\nبرای خروج دوبار روی جدول بزنید یا دکمه بازگشت را بزنید",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        // لمس برای جابجایی در حالت Move Mode
        holder.itemView.setOnLongClickListener {
            if (moveMode) {
                itemTouchHelper?.startDrag(holder)
            }
            false
        }
    }

    // 🧩 جابجایی آیتم‌ها
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val movedItem = transactions.removeAt(fromPosition)
        transactions.add(toPosition, movedItem)
        notifyItemMoved(fromPosition, toPosition)
        onOrderChanged(transactions)
    }
}
