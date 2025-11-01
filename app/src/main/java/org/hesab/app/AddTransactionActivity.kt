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

        val editDate = findViewById<EditText>(R.id.editDate)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val editCategory = findViewById<EditText>(R.id.editCategory)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        val radioExpense = findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = findViewById<RadioButton>(R.id.radioIncome)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // اگر ویرایش باشد، مقادیر را از Intent بگیریم
        editingId = intent.getIntExtra("id", -1).takeIf { it != -1 }
        if (editingId != null) {
            editDate.setText(intent.getStringExtra("date"))
            editAmount.setText(intent.getLongExtra("amount", 0).toString())
            editCategory.setText(intent.getStringExtra("category"))
            editDescription.setText(intent.getStringExtra("description"))
            val isIncome = intent.getBooleanExtra("isIncome", false)
            if (isIncome) radioIncome.isChecked = true else radioExpense.isChecked = true
        }

        btnSave.setOnClickListener {
            val date = editDate.text.toString().trim()
            val amount = editAmount.text.toString().toLongOrNull() ?: 0L
            val category = editCategory.text.toString().trim()
            val description = editDescription.text.toString().trim()
            val isIncome = radioIncome.isChecked

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
                val dao = TransactionDatabase.getDatabase(applicationContext).transactionDao()
                if (editingId == null) dao.insert(transaction)
                else dao.update(transaction)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }
}
