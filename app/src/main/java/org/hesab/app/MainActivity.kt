package org.hesab.app

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var btnAddTransaction: Button

    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getInstance(this)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddTransaction = findViewById(R.id.btnAddTransaction)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // دکمه افزودن تراکنش جدید
        btnAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        // دوبار لمس برای خروج از حالت جابجایی
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val now = System.currentTimeMillis()
                if (now - lastTapTime < 300) { // دوبار ضربه
                    if (adapter.isMoveMode()) {
                        adapter.setMoveMode(false)
                        Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
                    }
                }
                lastTapTime = now
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions() // هر بار بازگشت، لیست به‌روزرسانی شود
    }

    override fun onBackPressed() {
        // اگر در حالت جابجایی هستیم، فقط از حالت خارج شو
        if (::adapter.isInitialized && adapter.isMoveMode()) {
            adapter.setMoveMode(false)
            Toast.makeText(this, "حالت جابجایی غیرفعال شد", Toast.LENGTH_SHORT).show()
        } else {
            super.onBackPressed()
        }
    }

    private fun loadTransactions() {
        Thread {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    this,
                    transactions,
                    onEdit = { transaction -> editTransaction(transaction) },
                    onDelete = { transaction -> deleteTransaction(transaction) },
                    onOrderChanged = { updatedList -> saveOrderToDatabase(updatedList) }
                )
                recyclerView.adapter = adapter

                val touchHelper = ItemTouchHelper(object :
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
                touchHelper.attachToRecyclerView(recyclerView)
                adapter.attachTouchHelper(touchHelper)
            }
        }.start()
    }

    private fun editTransaction(transaction: Transaction) {
        val intent = Intent(this, AddTransactionActivity::class.java)
        intent.putExtra("edit_transaction_id", transaction.id)
        startActivity(intent)
    }

    private fun deleteTransaction(transaction: Transaction) {
        Thread {
            db.transactionDao().delete(transaction)
            runOnUiThread {
                Toast.makeText(this, "تراکنش حذف شد", Toast.LENGTH_SHORT).show()
                loadTransactions() // لیست را دوباره لود کن
            }
        }.start()
    }

    private fun saveOrderToDatabase(updatedList: List<Transaction>) {
        Thread {
            updatedList.forEachIndexed { index, transaction ->
                db.transactionDao().updateOrder(transaction.id, index)
            }
        }.start()
    }
}
