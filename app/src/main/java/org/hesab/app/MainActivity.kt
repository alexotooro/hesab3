package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadTransactions()

        btnAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { /* اینجا کد ویرایش بنویس */ },
                    onDelete = { /* اینجا کد حذف بنویس */ }
                )

                recyclerView.adapter = adapter

                // فعال کردن جابجایی ردیف‌ها
                val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ) {
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

                touchHelper.attachToRecyclerView(recyclerView)
                adapter.attachTouchHelper(touchHelper)
            }
        }.start()
    }
}
