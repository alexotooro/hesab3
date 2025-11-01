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

        editingId = intent.getIntExtra("transaction_id", -1).takeIf { it != -1 }
        if (editingId != null) {
            editDate.setText(intent.getStringExtra("transaction_date"))
            editAmount.setText(intent.getLongExtra("transaction_amount", 0L).toString())
            editCategory.setText(intent.getStringExtra("transaction_category"))
            editDescription.setText(intent.getStringExtra("transaction_description"))
            val isExpense = intent.getBooleanExtra("transaction_isExpense", true)
            radioExpense.isChecked = isExpense
            radioIncome.isChecked = !isExpense
        } else {
            radioExpense.isChecked = true
        }

        btnSave.setOnClickListener {
            val date = editDate.text.toString().trim()
            val amount = editAmount.text.toString().toLongOrNull() ?: 0L
            val category = editCategory.text.toString().trim()
            val description = editDescription.text.toString().trim()
            val isExpense = radioExpense.isChecked

            if (date.isEmpty() || amount <= 0) {
                Toast.makeText(this, "تاریخ و مبلغ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val dao = TransactionDatabase.getDatabase(this@AddTransactionActivity).transactionDao()
                if (editingId != null) {
                    val transaction = Transaction(
                        id = editingId!!,
                        date = date,
                        amount = amount,
                        category = category,
                        description = description,
                        isExpense = isExpense
                    )
                    dao.update(transaction)
                } else {
                    val transaction = Transaction(
                        id = 0,
                        date = date,
                        amount = amount,
                        category = category,
                        description = description,
                        isExpense = isExpense
                    )
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
