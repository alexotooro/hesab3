package org.hesab.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), TransactionAdapter.OnTransactionInteractionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private lateinit var tvBalance: TextView

    private val SMS_PERMISSIONS_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        val btnAdd = findViewById<Button>(R.id.btnAddTransaction)
        tvBalance = findViewById(R.id.tvBalance)

        db = AppDatabase.getInstance(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(mutableListOf(), this)
        recyclerView.adapter = adapter

        enableDragAndDrop()

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestSmsPermissionsIfNeeded()
    }

    override fun onResume() {
        super.onResume()
        loadTransactions()
    }

    private fun requestSmsPermissionsIfNeeded() {
        val perms = arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)
        val needed = perms.any { ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (needed) {
            ActivityCompat.requestPermissions(this, perms, SMS_PERMISSIONS_REQUEST)
        }
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll()
            var balance = 0L
            for (t in list) {
                balance += if (t.isIncome) t.amount else -t.amount
            }
            runOnUiThread {
                adapter.replaceAll(list)
                tvBalance.text = "مانده: ${formatAmount(balance)} ریال"
            }
        }
    }

    private fun formatAmount(amount: Long): String {
        return android.icu.text.NumberFormat.getInstance().format(amount)
    }

    override fun onEdit(transaction: Transaction) {
        val i = Intent(this, AddTransactionActivity::class.java)
        i.putExtra("transaction_id", transaction.id)
        startActivity(i)
    }

    override fun onDelete(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().delete(transaction)
            loadTransactions()
        }
    }

    override fun onRequestMoveMode() {
        // activate move mode: ItemTouchHelper already present; we can show a toast or snackbar
        // For a real move-mode UI, we toggle a flag that makes items draggable; our ItemTouchHelper uses drag by long-press by default.
        android.widget.Toast.makeText(this, "حالت جابجایی فعال است. برای خروج، روی لیست دابل‌کلیک کنید یا دکمه بازگشت را بزنید", android.widget.Toast.LENGTH_LONG).show()
    }

    private fun enableDragAndDrop() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.swap(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            override fun isLongPressDragEnabled(): Boolean = true
            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // after move complete, persist new order if needed
                persistOrder(adapter.getList())
            }
        }
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun persistOrder(list: List<Transaction>) {
        CoroutineScope(Dispatchers.IO).launch {
            // Recompute orderIndex based on position
            list.forEachIndexed { idx, tx ->
                val updated = tx.copy(orderIndex = idx)
                db.transactionDao().update(updated)
            }
        }
    }
}
