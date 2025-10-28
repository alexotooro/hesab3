package org.hesab.app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.util.*
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private lateinit var btnAdd: Button
    private lateinit var tvBalance: TextView
    private var balance = 0L
    private val df = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tableLayout = findViewById(R.id.tableLayout)
        btnAdd = findViewById(R.id.btnAdd)
        tvBalance = findViewById(R.id.tvBalance)

        btnAdd.setOnClickListener { showAddMenu() }
    }

    private fun showAddMenu() {
        val options = arrayOf("درآمد", "هزینه")
        AlertDialog.Builder(this)
            .setTitle("انتخاب نوع ثبت")
            .setItems(options) { _, which ->
                val isIncome = (which == 0)
                showInputDialog(isIncome)
            }
            .show()
    }

    private fun showInputDialog(isIncome: Boolean) {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 16, 32, 16)

        // تاریخ
        val dateLayout = LinearLayout(this)
        dateLayout.orientation = LinearLayout.HORIZONTAL

        val etDate = EditText(this)
        etDate.hint = "تاریخ (مثلاً 1404/08/01)"
        etDate.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        dateLayout.addView(etDate)

        val btnDate = Button(this)
        btnDate.text = "📅"
        btnDate.setOnClickListener { showDatePicker(etDate) }
        dateLayout.addView(btnDate)
        layout.addView(dateLayout)

        // مبلغ
        val etAmount = EditText(this)
        etAmount.hint = "مبلغ (ریال)"
        etAmount.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        layout.addView(etAmount)

        // افزودن جداکننده سه‌رقمی هنگام تایپ مبلغ
        etAmount.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etAmount.removeTextChangedListener(this)
                    val cleanString = s.toString().replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        val formatted = df.format(cleanString.toLong())
                        current = formatted
                        etAmount.setText(formatted)
                        etAmount.setSelection(formatted.length)
                    }
                    etAmount.addTextChangedListener(this)
                }
            }
        })

        // بابت
        // بابت
val etType = AutoCompleteTextView(this)
etType.hint = "بابت"

// لیست آماده
val items = arrayOf("خرج روزانه", "ماشین", "مدرسه", "درمان", "قسط", "حقوق", "سایر")

// آداپتر برای لیست
val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, items)
etType.setAdapter(adapter)

// باعث میشه لیست با یک کلیک باز بشه
etType.threshold = 0

// 🔹 اضافه کن:
etType.dropDownVerticalOffset = -300   // لیست به بالا باز میشه (مقدار رو می‌تونی کم‌وزیاد کنی)

// وقتی کاربر روی فیلد کلیک کنه، لیست باز میشه
etType.setOnClickListener {
    etType.showDropDown()
}

layout.addView(etType)


        // توضیحات
        val etDesc = EditText(this)
        etDesc.hint = "توضیحات"
        layout.addView(etDesc)

        AlertDialog.Builder(this)
            .setTitle(if (isIncome) "افزودن درآمد" else "افزودن هزینه")
            .setView(layout)
            .setPositiveButton("ثبت") { _, _ ->
                val date = etDate.text.toString()
                val amount = etAmount.text.toString().replace(",", "").toLongOrNull() ?: 0L
                val type = etType.text.toString()
                val desc = etDesc.text.toString()
                addRow(date, amount, type, desc, isIncome)
                updateBalance(amount, isIncome)
            }
            .setNegativeButton("انصراف", null)
            .show()
    }

    // نمایش انتخاب‌گر تاریخ (شمسی ساده)
    private fun showDatePicker(etDate: EditText) {
    val today = PersianDate() // تاریخ امروز شمسی
    val dp = DatePickerDialog(
        this,
        { _, year, month, day ->
            val selectedPersian = PersianDate()
            selectedPersian.shYear = year
            selectedPersian.shMonth = month + 1
            selectedPersian.shDay = day
            val fmt = PersianDateFormat("Y/m/d")
            etDate.setText(fmt.format(selectedPersian))
        },
        today.shYear,
        today.shMonth - 1,
        today.shDay
    )
    dp.setTitle("انتخاب تاریخ شمسی")
    dp.show()
}


    private fun addRow(date: String, amount: Long, type: String, desc: String, isIncome: Boolean) {
        val row = TableRow(this)
        val color = if (isIncome) "#D6EAF8" else "#FADBD8"
        row.setBackgroundColor(android.graphics.Color.parseColor(color))
        row.setPadding(4, 4, 4, 4)

        val tvDate = TextView(this)
        tvDate.text = date
        tvDate.gravity = Gravity.CENTER

        val tvAmount = TextView(this)
        tvAmount.text = df.format(amount)
        tvAmount.gravity = Gravity.CENTER

        val tvType = TextView(this)
        tvType.text = type
        tvType.gravity = Gravity.CENTER

        val tvDesc = TextView(this)
        tvDesc.text = desc
        tvDesc.gravity = Gravity.CENTER

        // ترتیب راست‌به‌چپ
        row.addView(tvDate)
        row.addView(tvAmount)
        row.addView(tvType)
        row.addView(tvDesc)

        tableLayout.addView(row)
    }

    private fun updateBalance(amount: Long, isIncome: Boolean) {
        balance += if (isIncome) amount else -amount
        tvBalance.text = "مانده: ${df.format(balance)} ریال"
    }
}
