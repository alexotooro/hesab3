package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var dao: TransactionDao
    private lateinit var etAmount: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var tvDate: TextView
    private lateinit var rbExpense: RadioButton
    private lateinit var rbIncome: RadioButton
    private lateinit var btnSave: Button

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale("fa", "IR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // 🧠 اتصال به Room Database
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "transactions.db"
        ).allowMainThreadQueries().build()

        dao = db.transactionDao()

        // 📦 اتصال به ویوها
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)
        tvDate = findViewById(R.id.tvDate)
        rbExpense = findViewById(R.id.rbExpense)
        rbIncome = findViewById(R.id.rbIncome)
        btnSave = findViewById(R.id.btnSave)

        // 🔹 پیش‌فرض: هزینه انتخاب شود
        rbExpense.isChecked = true

        // 🔹 تاریخ پیش‌فرض: امروز
        tvDate.text = dateFormat.format(calendar.time)

        // 📅 انتخاب تاریخ با دیالوگ
        tvDate.setOnClickListener {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, y, m, d ->
                    calendar.set(y, m, d)
                    tvDate.text = dateFormat.format(calendar.time)
                },
                year, month, day
            )
            datePicker.show()
        }

        // 💾 ذخیره تراکنش
        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            val category = etCategory.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val date = tvDate.text.toString()

            if (amountText.isEmpty()) {
                Toast.makeText(this, "لطفاً مبلغ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toLongOrNull() ?: 0L
            val isExpense = rbExpense.isChecked
            val type = if (isExpense) "expense" else "income"

            // 🔹 تعیین orderIndex برای مرتب‌سازی
            val maxOrder = dao.getMaxOrderIndex() ?: 0

            // 📦 ساخت تراکنش جدید
            val transaction = Transaction(
                date = date,
                amount = amount,
                category = category.ifEmpty { "بدون دسته‌بندی" },
                description = description,
                type = type,
                orderIndex = maxOrder + 1
            )

            // 💾 درج در دیتابیس
            dao.insert(transaction)

            Toast.makeText(this, "تراکنش با موفقیت ذخیره شد", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
