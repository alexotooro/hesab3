package org.hesab.app

import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: AppDatabase
    private var editTransactionId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        // اگر در حالت ویرایش هستیم
        editTransactionId = intent.getIntExtra("edit_transaction_id", -1)
        if (editTransactionId != -1) {
            loadTransactionForEdit(editTransactionId!!)
        }

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun loadTransactionForEdit(id: Int) {
        Thread {
            val transaction = db.transactionDao().getById(id)
            transaction?.let {
                runOnUiThread {
                    binding.etDate.setText(it.date)
                    binding.etAmount.setText(it.amount.toString())
                    binding.etCategory.setText(it.category)
                    binding.etDescription.setText(it.description)

                    if (it.type == "درآمد") {
                        binding.radioIncome.isChecked = true
                    } else {
                        binding.radioExpense.isChecked = true
                    }

                    binding.btnSave.text = "ویرایش تراکنش"
                }
            }
        }.start()
    }

    private fun saveTransaction() {
        val date = binding.etDate.text.toString()
        val amount = binding.etAmount.text.toString().toDoubleOrNull() ?: 0.0
        val category = binding.etCategory.text.toString()
        val description = binding.etDescription.text.toString()
        val type = findViewById<RadioButton>(binding.radioGroupType.checkedRadioButtonId).text.toString()

        if (date.isBlank() || category.isBlank()) {
            Toast.makeText(this, "لطفاً تمام فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            if (editTransactionId != null && editTransactionId != -1) {
                // حالت ویرایش
                val transaction = Transaction(editTransactionId!!, date, amount, category, description, type)
                db.transactionDao().update(transaction)
            } else {
                // حالت افزودن جدید
                val transaction = Transaction(0, date, amount, category, description, type)
                db.transactionDao().insert(transaction)
            }

            runOnUiThread {
                Toast.makeText(this, "ذخیره شد", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.start()
    }
}
