package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        db = AppDatabase.getInstance(this)

        val radioIncome = findViewById<RadioButton>(R.id.radioIncome)
        val radioExpense = findViewById<RadioButton>(R.id.radioExpense)
        val etDate = findViewById<EditText>(R.id.etDate)
        val btnPickDate = findViewById<ImageButton>(R.id.btnPickDate)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etNote = findViewById<EditText>(R.id.etNote)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // تاریخ امروز پیش‌فرض
        val cal = Calendar.getInstance()
        etDate.setText(dateFormat.format(cal.time))

        btnPickDate.setOnClickListener {
            val now = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                val picked = Calendar.getInstance()
                picked.set(y, m, d)
                etDate.setText(dateFormat.format(picked.time))
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString().replace(",", "").trim()
            val amount = try { amountText.toLong() } catch (e: Exception) { 0L }
            val category = etCategory.text.toString().ifBlank { "بدون عنوان" }
            val note = etNote.text.toString().ifBlank { "" }
            val isIncome = radioIncome.isChecked
            val dateString = etDate.text.toString().trim()

            // تاریخ را به timestamp تبدیل می‌کنیم (درصورت خطا، زمان فعلی را می‌گیرد)
            val dateMillis = try {
                dateFormat.parse(dateString)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }

            val txn = Transaction(
                date = dateMillis,
                amount = amount,
                category = category,
                note = note,
                isIncome = isIncome,
                orderIndex = 0
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.transactionDao().insert(txn)
                runOnUiThread { finish() }
            }
        }
    }
}
