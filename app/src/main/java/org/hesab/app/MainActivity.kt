package org.hesab.app
import androidx.recyclerview.widget.ItemTouchHelper
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var db: AppDatabase
    private var transactions = mutableListOf<Transaction>()

    companion object {
        private const val ADD_EDIT_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔹 اعمال فونت و تم قبل از setContentView
        ThemeHelper.applyTheme(this)
        FontHelper.applyFont(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TransactionAdapter(transactions, db)
        recyclerView.adapter = adapter

        // افزودن تراکنش جدید
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE)
        }

        // کشیدن و جابجایی ردیف‌ها
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                adapter.moveItem(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        loadTransactions()
        setupFilters()
    }

    private fun loadTransactions() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = db.transactionDao().getAll()
            withContext(Dispatchers.Main) {
                transactions.clear()
                transactions.addAll(list)
                adapter.notifyDataSetChanged()
                updateBalance()
            }
        }
    }

    private fun setupFilters() {
        val chkOnlyExpense = findViewById<CheckBox>(R.id.chkOnlyExpense)
        val chkOnlyIncome = findViewById<CheckBox>(R.id.chkOnlyIncome)

        chkOnlyExpense.setOnCheckedChangeListener { _, _ -> loadTransactions() }
        chkOnlyIncome.setOnCheckedChangeListener { _, _ -> loadTransactions() }
    }

    private fun updateBalance() {
        val chkOnlyExpense = findViewById<CheckBox>(R.id.chkOnlyExpense)
        val chkOnlyIncome = findViewById<CheckBox>(R.id.chkOnlyIncome)

        val filteredTransactions = transactions.filter {
            (chkOnlyExpense.isChecked && !it.isIncome) || (chkOnlyIncome.isChecked && it.isIncome)
        }

        val totalBalance = filteredTransactions.sumOf { if (it.isIncome) it.amount else -it.amount }
        findViewById<TextView>(R.id.tvBalance).text = "مانده: ${totalBalance} ریال"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadTransactions() // ✅ رفرش خودکار بعد از افزودن یا ویرایش
        }
    }

    override fun onResume() {
        super.onResume()
        loadTransactions() // اطمینان از رفرش هنگام بازگشت
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.menu_sms -> {
                startActivity(Intent(this, SmsListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
