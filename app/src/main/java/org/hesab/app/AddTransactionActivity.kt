package org.hesab.app

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var edtDate: EditText
    private lateinit var edtAmount: EditText
    private lateinit var edtCategory: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var db: AppDatabase
    private var editingId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction) // شما باید این layout را قرار بدهی مطابق عکس شماره 5

        db = AppDatabase.getInstance(this)

        edtDate = findViewById(R.id.edtDate)
        edtAmount = findViewById(R.id.edtAmount)
        edtCategory = findViewById(R.id.edtCategory)
        edtDescription = findViewById(R.id.edtDescription)
        btnSave = findViewById(R.id.btnSave)

        // numeric formatting: add text watcher
        edtAmount.addTextChangedListener(ThousandsTextWatcher(edtAmount))

        // focus on date initially
        edtDate.requestFocus()

        // handle prefill from notification/sms
        val prefill = intent.getBooleanExtra("prefill_from_sms", false)
        if (prefill) {
            val id = intent.getLongExtra("transaction_id_prefill", -1L)
            val bankGuess = intent.getStringExtra("bank_guess") ?: "صادرات"
            if (id != -1L) {
                editingId = id
                CoroutineScope(Dispatchers.IO).launch {
                    val tx = db.transactionDao().getById(id)
                    tx?.let {
                        runOnUiThread {
                            edtDate.setText(it.date)
                            edtAmount.setText(it.amount.toString())
                            edtCategory.setText(it.category)
                            edtDescription.setText(it.description)
                        }
                    }
                }
            } else {
                // if not inserted yet, maybe prefill fields from extras (amount etc.)
            }
        } else {
            val tid = intent.getIntExtra("transaction_id", -1)
            if (tid != -1) {
                editingId = tid.toLong()
                CoroutineScope(Dispatchers.IO).launch {
                    val tx = db.transactionDao().getById(editingId)
                    tx?.let {
                        runOnUiThread {
                            edtDate.setText(it.date)
                            edtAmount.setText(it.amount.toString())
                            edtCategory.setText(it.category)
                            edtDescription.setText(it.description)
                        }
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun saveTransaction() {
        val date = edtDate.text.toString().ifBlank { PersianDateUtil.todayShamsi() }
        val amountRaw = edtAmount.text.toString().replace(Regex("[^0-9]"), "")
        val amount = if (amountRaw.isBlank()) 0L else amountRaw.toLong()
        val category = edtCategory.text.toString().ifBlank { "سایر" }
        val desc = edtDescription.text.toString().ifBlank { "" }
        val isIncome = false // you can read radio button to set true/false

        val tx = Transaction(
            date = date,
            amount = amount,
            category = category,
            description = desc,
            isIncome = isIncome
        )

        CoroutineScope(Dispatchers.IO).launch {
            if (editingId > 0) {
                val existing = db.transactionDao().getById(editingId)
                if (existing != null) {
                    val updated = existing.copy(date = date, amount = amount, category = category, description = desc)
                    db.transactionDao().update(updated)
                }
            } else {
                db.transactionDao().insert(tx)
            }
            runOnUiThread { finish() }
        }
    }
}
