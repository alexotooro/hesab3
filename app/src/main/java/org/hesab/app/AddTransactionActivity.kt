package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var tvDate: EditText
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var btnSave: Button

    private var editingTransactionId: Int? = null
    private val dao by lazy { AppDatabase.getInstance(this).transactionDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        tvDate = findViewById(R.id.tvDate)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)
        rbIncome = findViewById(R.id.rbIncome)
        rbExpense = findViewById(R.id.rbExpense)
        btnSave = findViewById(R.id.btnSave)

        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        tvDate.setText(sdf.format(Date()))

        tvDate.setOnClickListener {
            val c = Calendar.getInstance()
            val dialog = DatePickerDialog(this,
                { _, y, m, d ->
                    tvDate.setText(String.format("%04d/%02d/%02d", y, m + 1, d))
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        editingTransactionId = intent.getIntExtra("transactionId", -1).takeIf { it != -1 }
        editingTransactionId?.let { id ->
            lifecycleScope.launch(Dispatchers.IO) {
                val t = dao.getById(id)
                withContext(Dispatchers.Main) {
                    t?.let {
                        tvDate.setText(it.date)
                        etAmount.setText(it.amount.toString())
                        etCategory.setText(it.category)
                        etDescription.setText(it.description)
                        if (it.type == "income") rbIncome.isChecked = true else rbExpense.isChecked = true
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val date = tvDate.text.toString()
            val amountText = etAmount.text.toString()
            val category = etCategory.text.toString()
            val description = etDescription.text.toString()
            val type = if (rbIncome.isChecked) "income" else "expense"

            if (amountText.isBlank() || category.isBlank()) {
                Toast.makeText(this, "لطفاً مبلغ و بابت را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toLongOrNull() ?: 0L

            lifecycleScope.launch(Dispatchers.IO) {
                if (editingTransactionId != null) {
                    val existingOrder = dao.getOrderIndexById(editingTransactionId!!) ?: 0
                    dao.update(
                        Transaction(
                            id = editingTransactionId!!,
                            date = date,
                            amount = amount,
                            category = category,
                            description = description,
                            type = type,
                            orderIndex = existingOrder
                        )
                    )
                } else {
                    val maxOrder = dao.getMaxOrderIndex() ?: 0
                    dao.insert(
                        Transaction(
                            date = date,
                            amount = amount,
                            category = category,
                            description = description,
                            type = type,
                            orderIndex = maxOrder + 1
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddTransactionActivity, "تراکنش ذخیره شد", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}
