// app/src/main/java/org/hesab/app/AddTransactionActivity.kt
package org.hesab.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

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

        intent?.let {
            editAmount.setText(it.getStringExtra("amount") ?: "")
            editDate.setText(it.getStringExtra("date") ?: "")
            editCategory.setText(it.getStringExtra("category") ?: "")
            editDescription.setText(it.getStringExtra("description") ?: "")
        }

        btnSave.setOnClickListener {
            val date = editDate.text.toString()
            val amount = editAmount.text.toString().toLongOrNull() ?: 0
            val category = editCategory.text.toString()
            val description = editDescription.text.toString()
            val isIncome = radioIncome.isChecked

            val transaction = Transaction(
                date = date,
                amount = amount,
                category = category,
                description = description,
                isIncome = isIncome,
                orderIndex = 0
            )

            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(this@AddTransactionActivity)
                db.transactionDao().insert(transaction)
                finish()
            }
        }
    }
}
