package org.hesab.app

import android.content.Context
import android.view.*
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
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var touchHelper: ItemTouchHelper? = null
    private var isMoveMode = false

    fun attachTouchHelper(helper: ItemTouchHelper) {
        touchHelper = helper
    }

    fun moveItem(from: Int, to: Int) {
        val item = transactions.removeAt(from)
        transactions.add(to, item)
        notifyItemMoved(from, to)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val t = transactions[position]
        holder.txtDate.text = t.date
        holder.txtAmount.text = t.amount.toString()
        holder.txtCategory.text = t.category
        holder.txtDescription.text = t.description

        holder.btnMore.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.menu_transaction_item, popup.menu)

            // اضافه کردن گزینه جابجایی
            popup.menu.add("جابجایی")

            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "ویرایش" -> onEdit(t)
                    "حذف" -> onDelete(t)
                    "جابجایی" -> {
                        isMoveMode = true
                        notifyDataSetChanged()
                    }
                }
                true
            }
            popup.show()
        }

        holder.itemView.setOnLongClickListener {
            if (isMoveMode) {
                touchHelper?.startDrag(holder)
            }
            false
        }
    }

    fun exitMoveMode() {
        isMoveMode = false
        notifyDataSetChanged()
    }

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtAmount: TextView = view.findViewById(R.id.txtAmount)
        val txtCategory: TextView = view.findViewById(R.id.txtCategory)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
    }
}
