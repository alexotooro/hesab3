package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SmsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SmsAdapter
    private lateinit var db: AppDatabase
    private var smsList = mutableListOf<Transaction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_list)
        title = "پیامک‌های دریافتی"

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerViewSms)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SmsAdapter(smsList)
        recyclerView.adapter = adapter

        loadSmsTransactions()
    }

    private fun loadSmsTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll().filter {
                it.description.contains("پیامک", ignoreCase = true)
            }
            withContext(Dispatchers.Main) {
                smsList.clear()
                smsList.addAll(list)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
