package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvDate: EditText
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var btnSave: Button

    private var transactionId: Int? = null
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)
        tvDate = findViewById(R.id.tvDate)
        rbIncome = findViewById(R.id.rbIncome)
        rbExpense = findViewById(R.id.rbExpense)
        btnSave = findViewById(R.id.btnSave)

        db = AppDatabase.getInstance(this)

        tvDate.setOnClickListener { showDatePicker() }

        transactionId = intent.getIntExtra("transaction_id", -1).takeIf { it != -1 }

        transactionId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val transaction = db.transactionDao().getById(id)
                transaction?.let {
                    runOnUiThread {
                        etAmount.text = Editable.Factory.getInstance().newEditable(it.amount.toString())
                        etCategory.text = Editable.Factory.getInstance().newEditable(it.category)
                        etDescription.text = Editable.Factory.getInstance().newEditable(it.description)
                        tvDate.text = Editable.Factory.getInstance().newEditable(it.date)
                        if (it.isIncome) rbIncome.isChecked = true else rbExpense.isChecked = true
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toLongOrNull() ?: 0L
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val date = tvDate.text.toString()
            val isIncome = rbIncome.isChecked

            CoroutineScope(Dispatchers.IO).launch {
                if (transactionId != null) {
                    val existing = db.transactionDao().getById(transactionId!!)
                    existing?.let {
                        val updated = it.copy(
                            date = date,
                            amount = amount,
                            category = category,
                            description = description,
                            isIncome = isIncome
                        )
                        db.transactionDao().update(updated)
                    }
                } else {
                    val newOrder = (db.transactionDao().getMaxOrderIndex() ?: 0) + 1
                    val transaction = Transaction(
                        date = date,
                        amount = amount,
                        category = category,
                        description = description,
                        isIncome = isIncome,
                        orderIndex = newOrder
                    )
                    db.transactionDao().insert(transaction)
                }
                finish()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                tvDate.setText("$year/${month + 1}/$day")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}
