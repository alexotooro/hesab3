package org.hesab.app

import android.content.Context
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat

class TransactionAdapter(
    private val transactions: MutableList<Transaction>,
    private val context: Context
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int = transactions.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]

        holder.tvDate.text = item.date
        holder.tvAmount.text = "%,d ریال".format(item.amount)
        holder.tvCategory.text = item.category
        holder.tvDescription.text = item.description

        val prefs = context.getSharedPreferences("hesab_settings", Context.MODE_PRIVATE)
        val showLines = prefs.getBoolean("showLines", true)
        val alternateRows = prefs.getBoolean("alternateRows", false)
        val showInTomans = prefs.getBoolean("showInTomans", false)

        if (showInTomans) {
            holder.tvAmount.text = "%,d تومان".format(item.amount / 10)
        }

        if (alternateRows && position % 2 == 1) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.rowAlternate))
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        if (!showLines) {
            holder.itemView.findViewById<View>(R.id.divider)?.visibility = View.GONE
        } else {
            holder.itemView.findViewById<View>(R.id.divider)?.visibility = View.VISIBLE
        }

        holder.tvAmount.setTextColor(
            ContextCompat.getColor(context,
                if (item.isIncome) R.color.incomeGreen else R.color.expenseRed
            )
        )
    }
}
