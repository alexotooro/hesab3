package org.hesab.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // توجه: اگر در layout آی‌دی toolbar متفاوت است (مثلاً topAppBar) اینجا آن id را بگذار
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // اگر می‌خواهی روی همان آیکن منو (+) در toolbar گوش بدهی، بهتر منو را inflate و onOptionsItemSelected را استفاده کنی.
        // RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // دیتابیس (بسته به نسخه‌ای که در پروژه تو هست این متد ممکن است getInstance یا getDatabase باشد).
        // این پروژه‌ای که آخرین نسخه‌اش را فرستادی تابعش به نام getDatabase بود.
        db = AppDatabase.getInstance(this)


        loadTransactions()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val transactions = db.transactionDao().getAll().toMutableList()
            launch(Dispatchers.Main) {
                // adapter باید مطابق امضای فعلی تو ساخته بشه.
                // اگر نسخه‌ی TransactionAdapter تو فقط یک پارامتر می‌پذیرد، آن را به شکل زیر تغییر بده:
                // adapter = TransactionAdapter(transactions)
                // در صورتی که امضای adapter سه پارامتری است (transactions, onEdit, onDelete) از نسخه‌ی زیر استفاده کن:
                adapter = try {
                    TransactionAdapter(
                        transactions,
                        onEdit = { /* TODO: باز کردن صفحه ویرایش */ },
                        onDelete = { transaction ->
                            CoroutineScope(Dispatchers.IO).launch {
                                db.transactionDao().delete(transaction)
                                transactions.remove(transaction)
                                launch(Dispatchers.Main) { adapter.notifyDataSetChanged() }
                            }
                        }
                    )
                } catch (e: NoSuchMethodError) {
                    // اگر نسخه‌ی قدیمی Adapter داری که فقط لیست می‌گیرد، این شاخه اجرا می‌شود:
                    @Suppress("UNCHECKED_CAST")
                    TransactionAdapter(transactions as MutableList<Transaction>, {}, {})
                }
                recyclerView.adapter = adapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu) // فایل منو باید وجود داشته باشد (main_menu.xml)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, AddTransactionActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
