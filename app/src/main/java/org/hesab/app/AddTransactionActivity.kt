package org.hesab.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        db = AppDatabase.getInstance(this)

        val rbExpense = findViewById<RadioButton>(R.id.rbExpense)
        val rbIncome = findViewById<RadioButton>(R.id.rbIncome)
        val edtDate = findViewById<EditText>(R.id.edtDate)
        val edtAmount = findViewById<EditText>(R.id.edtAmount)
        val edtCategory = findViewById<EditText>(R.id.edtCategory)
        val edtDescription = findViewById<EditText>(R.id.edtDescription)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // پیش‌فرض روی هزینه
        rbExpense.isChecked = true

        btnSave.setOnClickListener {
            val type = if (rbIncome.isChecked) "درآمد" else "هزینه"
            val date = edtDate.text.toString()
            val amountText = edtAmount.text.toString()
            val category = edtCategory.text.toString()
            val description = edtDescription.text.toString()

            if (date.isEmpty() || amountText.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "لطفاً همه فیلدهای لازم را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "مبلغ نامعتبر است", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val transaction = Transaction(
                type = type,
                date = date,
                amount = amount,
                category = category,
                description = description
            )

            // ✅ اجرای در Thread جدا تا روی اندروید 7 کرش نکنه
            Thread {
                db.transactionDao().insert(transaction)
                runOnUiThread {
                    Toast.makeText(this, "تراکنش با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }
    }
}
