package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import java.util.Calendar

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: AppDatabase
    private var editTransactionId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getInstance(this)

        // اگر تراکنش برای ویرایش فرستاده شده باشد
        editTransactionId = intent.getIntExtra("edit_transaction_id", -1).takeIf { it != -1 }

        // انتخاب تاریخ با DatePicker
        binding.tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val dp = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val formatted = "%04d/%02d/%02d".format(year, month + 1, dayOfMonth)
                    binding.tvDate.text = formatted
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }

        // اگر ویرایش است، مقادیر را پر کن
        editTransactionId?.let { id ->
            Thread {
                val transaction = db.transactionDao().getById(id)
                transaction?.let {
                    runOnUiThread {
                        binding.tvDate.text = it.date
                        binding.etAmount.setText(it.amount.toString())
                        binding.etCategory.setText(it.category)
                        binding.etDescription.setText(it.description)
                        if (it.type == "درآمد") binding.rbIncome.isChecked = true
                        else binding.rbExpense.isChecked = true
                    }
                }
            }.start()
        }

        // دکمه ذخیره
        binding.btnSave.setOnClickListener {
            val type = if (binding.rbIncome.isChecked) "درآمد" else "هزینه"
            val date = binding.tvDate.text.toString()
            val amount = binding.etAmount.text.toString().toLongOrNull() ?: 0L
            val category = binding.etCategory.text.toString()
            val description = binding.etDescription.text.toString()

            if (date.isBlank() || amount <= 0) {
                Toast.makeText(this, "لطفاً تاریخ و مبلغ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Thread {
                if (editTransactionId != null) {
                    val existing = db.transactionDao().getById(editTransactionId!!)
                    if (existing != null) {
                        val updated = existing.copy(
                            date = date,
                            amount = amount,
                            category = category,
                            description = description,
                            type = type
                        )
                        db.transactionDao().update(updated)
                    }
                } else {
                    // برای تراکنش جدید، orderIndex را بالاترین مقدار فعلی + 1 بگذار
                    val maxOrder = db.transactionDao().getMaxOrderIndex() ?: 0
                    val transaction = Transaction(
                        date = date,
                        amount = amount,
                        category = category,
                        description = description,
                        type = type,
                        orderIndex = maxOrder + 1
                    )
                    db.transactionDao().insert(transaction)
                }

                runOnUiThread {
                    Toast.makeText(this, "ذخیره شد", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }.start()
        }
    }
}
