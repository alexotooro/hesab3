package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import java.util.*

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTransactionBinding
    private lateinit var db: AppDatabase
    private var selectedDate: Date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dp = DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(y, m, d)
                    selectedDate = cal.time
                    binding.etDate.setText("${y}/${m + 1}/${d}")
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            dp.show()
        }

        binding.btnSave.setOnClickListener {
            val amountText = binding.etAmount.text.toString()
            val category = binding.etCategory.text.toString()
            val note = binding.etNote.text.toString()

            if (amountText.isEmpty()) {
                Toast.makeText(this, "مبلغ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toLongOrNull() ?: 0L
            val isIncome = binding.radioIncome.isChecked

            val transaction = Transaction(
                date = selectedDate,
                amount = amount,
                category = category,
                note = note,
                isIncome = isIncome
            )

            Thread {
                db.transactionDao().insert(transaction)
                runOnUiThread { finish() }
            }.start()
        }
    }
}
