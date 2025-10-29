package org.hesab.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        // پیشفرض روی هزینه
        binding.rbExpense.isChecked = true

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val type = if (binding.rbIncome.isChecked) "درآمد" else "هزینه"
        val date = binding.edtDate.text.toString()
        val amountText = binding.edtAmount.text.toString()
        val category = binding.edtCategory.text.toString()
        val description = binding.edtDescription.text.toString()

        if (date.isEmpty() || amountText.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "لطفاً همه فیلدهای لازم را پر کنید", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "مبلغ نامعتبر است", Toast.LENGTH_SHORT).show()
            return
        }

        val transaction = Transaction(
            type = type,
            date = date,
            amount = amount,
            category = category,
            description = description
        )

        // ✅ انجام عملیات دیتابیس در Thread جدا برای جلوگیری از کرش در Android 7
        Thread {
            db.transactionDao().insert(transaction)
            runOnUiThread {
                Toast.makeText(this, "تراکنش با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }
}
