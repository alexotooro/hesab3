package org.hesab.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        title = "تراکنش جدید"

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etDate = findViewById<EditText>(R.id.etDate)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val rbIncome = findViewById<RadioButton>(R.id.rbIncome)
        val rbExpense = findViewById<RadioButton>(R.id.rbExpense)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        etDate.setText(sdf.format(Date()))

        val extras = intent.extras
        extras?.let {
            etAmount.setText(it.getString("amount", ""))
            etDate.setText(it.getString("date", sdf.format(Date())))
            etCategory.setText(it.getString("category", ""))
            etDescription.setText(it.getString("description", ""))
        }

        rbExpense.isChecked = true

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().replace(",", "").toLongOrNull()
            val date = etDate.text.toString()
            val category = etCategory.text.toString()
            val desc = etDescription.text.toString()
            val isIncome = rbIncome.isChecked

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "مبلغ نامعتبر است", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                date = date,
                amount = amount,
                category = category.ifBlank { "سایر" },
                description = desc,
                isIncome = isIncome,
                orderIndex = 0
            )

            CoroutineScope(Dispatchers.IO).launch {
                App.db.transactionDao().insert(transaction)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddTransactionActivity, "تراکنش ذخیره شد", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
