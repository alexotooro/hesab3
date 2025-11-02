package org.hesab.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val items: MutableList<Transaction>,
    private val onEdit: (Transaction) -> Unit = {},
    private val onDelete: (Transaction) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val t = items[position]
        holder.tvDate.text = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(t.date)
        holder.tvAmount.text = NumberFormat.getInstance().format(t.amount)
        holder.tvCategory.text = t.category
        holder.tvNote.text = t.note ?: ""

        holder.itemView.setOnLongClickListener {
            // popup menu: edit / delete
            val popup = PopupMenu(holder.itemView.context, holder.itemView)
            popup.menu.add("ویرایش")
            popup.menu.add("حذف")
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.title) {
                    "ویرایش" -> onEdit(t)
                    "حذف" -> {
                        // حذف از DB و لیست
                        onDelete(t)
                    }
                }
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = items.size
}
