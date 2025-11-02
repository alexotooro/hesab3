package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.material.appbar.MaterialToolbar
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_add -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = db.transactionDao().getAll().toMutableList()
            launch(Dispatchers.Main) {
                adapter = TransactionAdapter(
                    transactions,
                    onEdit = { transaction ->
                        // بعداً: باز کردن صفحه ویرایش
                    },
                    onDelete = { transaction ->
                        CoroutineScope(Dispatchers.IO).launch {
                            db.transactionDao().delete(transaction)
                            transactions.remove(transaction)
                            launch(Dispatchers.Main) { adapter.notifyDataSetChanged() }
                        }
                    }
                )
                recyclerView.adapter = adapter
            }
        }
    }
}
