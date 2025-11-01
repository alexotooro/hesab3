package org.hesab.app

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private var editingId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔹 اعمال تم و فونت قبل از setContentView
        ThemeHelper.applyTheme(this)
        FontHelper.applyFont(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // هماهنگ با شناسه‌های جدید XML
        val etDate = findViewById<EditText>(R.id.etDate)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val rbExpense = findViewById<RadioButton>(R.id.rbExpense)
        val rbIncome = findViewById<RadioButton>(R.id.rbIncome)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // اگر در حالت ویرایش هستیم
        editingId = intent.getIntExtra("id", -1).takeIf { it != -1 }
        if (editingId != null) {
            etDate.setText(intent.getStringExtra("date"))
            etAmount.setText(intent.getLongExtra("amount", 0).toString())
            etCategory.setText(intent.getStringExtra("category"))
            etDescription.setText(intent.getStringExtra("description"))
            val isIncome = intent.getBooleanExtra("isIncome", false)
            if (isIncome) rbIncome.isChecked = true else rbExpense.isChecked = true
        }

        btnSave.setOnClickListener {
            val date = etDate.text.toString().trim()
            val amount = etAmount.text.toString().toLongOrNull() ?: 0L
            val category = etCategory.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val isIncome = rbIncome.isChecked

            if (date.isEmpty() || amount == 0L || category.isEmpty()) {
                Toast.makeText(this, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                id = editingId ?: 0,
                date = date,
                amount = amount,
                category = category,
                description = description,
                isIncome = isIncome,
                orderIndex = 0
            )

            CoroutineScope(Dispatchers.IO).launch {
                val dao = AppDatabase.getDatabase(applicationContext).transactionDao()
                if (editingId == null) dao.insert(transaction)
                else dao.update(transaction)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
