package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var tvBalance: TextView
    private lateinit var chkOnlyExpense: CheckBox
    private lateinit var chkOnlyIncome: CheckBox
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        recyclerView = findViewById(R.id.recyclerView)
        tvBalance = findViewById(R.id.tvBalance)
        chkOnlyExpense = findViewById(R.id.chkOnlyExpense)
        chkOnlyIncome = findViewById(R.id.chkOnlyIncome)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)

        adapter = TransactionAdapter(
            db.transactionDao().getAll(),
            onDelete = { transaction ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.transactionDao().delete(transaction)
                    refreshData()
                }
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        chkOnlyExpense.setOnCheckedChangeListener { _, _ -> refreshData() }
        chkOnlyIncome.setOnCheckedChangeListener { _, _ -> refreshData() }

        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.moveItem(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        refreshData()
    }

    private fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll()
            runOnUiThread {
                adapter.updateData(list)
                val balance = list.sumOf { if (it.type == "expense") -it.amount else it.amount }
                tvBalance.text = "مانده: $balance ریال"
            }
        }
    }
}
