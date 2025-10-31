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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val editDate = findViewById<EditText>(R.id.editDate)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val editCategory = findViewById<EditText>(R.id.editCategory)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        val radioExpense = findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = findViewById<RadioButton>(R.id.radioIncome)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // بررسی اگر در حالت ویرایش باز شده باشد
        intent?.let {
            if (it.hasExtra("transaction_id")) {
                editingId = it.getIntExtra("transaction_id", 0)
                editDate.setText(it.getStringExtra("date") ?: "")
                editAmount.setText(it.getStringExtra("amount") ?: "")
                editCategory.setText(it.getStringExtra("category") ?: "")
                editDescription.setText(it.getStringExtra("description") ?: "")
                val isIncome = it.getBooleanExtra("isIncome", false)
                radioIncome.isChecked = isIncome
                radioExpense.isChecked = !isIncome
            }
        }

        btnSave.setOnClickListener {
            val date = editDate.text.toString()
            val amount = editAmount.text.toString().toLongOrNull() ?: 0L
            val category = editCategory.text.toString()
            val description = editDescription.text.toString()
            val isIncome = radioIncome.isChecked

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
                val db = AppDatabase.getDatabase(this@AddTransactionActivity)
                val dao = db.transactionDao()

                if (editingId != null && editingId != 0) {
                    dao.update(transaction)
                } else {
                    dao.insert(transaction)
                }

                runOnUiThread {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}
