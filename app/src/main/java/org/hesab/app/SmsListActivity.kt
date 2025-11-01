package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsListActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_list)

        db = AppDatabase.getInstance(this)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        CoroutineScope(Dispatchers.IO).launch {
            val transactions = db.transactionDao().getAll().toMutableList()
            runOnUiThread {
                adapter = TransactionAdapter(
                    transactions,
                    onEdit = { /* در صورت نیاز بعداً اضافه می‌کنیم */ },
                    onDelete = { /* در صورت نیاز بعداً اضافه می‌کنیم */ }
                )
                recyclerView.adapter = adapter
            }
        }
    }
}
