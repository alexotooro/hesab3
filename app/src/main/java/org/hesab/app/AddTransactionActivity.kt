package org.hesab.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog
import java.util.*

class AddTransactionActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var radioExpense: RadioButton
    private lateinit var radioIncome: RadioButton
    private lateinit var editTextAmount: EditText
    private lateinit var buttonSave: Button
    private lateinit var edtDate: EditText
    private lateinit var btnPickDate: ImageButton

    private var transactionType: String = "expense"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        radioExpense = findViewById(R.id.radioExpense)
        radioIncome = findViewById(R.id.radioIncome)
        editTextAmount = findViewById(R.id.edtAmount)
        buttonSave = findViewById(R.id.btnSave)
        edtDate = findViewById(R.id.edtDate)
        btnPickDate = findViewById(R.id.btnPickDate)

        // انتخاب نوع تراکنش
        radioExpense.setOnClickListener { transactionType = "expense" }
        radioIncome.setOnClickListener { transactionType = "income" }

        // جدا کردن سه‌رقمی مبلغ
        editTextAmount.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editTextAmount.removeTextChangedListener(this)

                    val cleanString = s.toString().replace(",", "")
                    if (cleanString.isNotEmpty()) {
                        val formatted = DecimalFormat("#,###").format(cleanString.toLong())
                        current = formatted
                        editTextAmount.setText(formatted)
                        editTextAmount.setSelection(formatted.length)
                    }

                    editTextAmount.addTextChangedListener(this)
                }
            }
        })

        // انتخاب تاریخ از تقویم شمسی (برای فیلد و دکمه)
        val datePickerListener = {
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show(supportFragmentManager, "Datepickerdialog")
        }

        edtDate.setOnClickListener { datePickerListener.invoke() }
        btnPickDate.setOnClickListener { datePickerListener.invoke() }

        // دکمه ثبت
        buttonSave.setOnClickListener {
            val typeText = if (transactionType == "expense") "هزینه" else "درآمد"
            val amount = editTextAmount.text.toString()
            val date = edtDate.text.toString()
            Toast.makeText(this, "$typeText با مبلغ $amount در تاریخ $date ثبت شد", Toast.LENGTH_SHORT).show()
        }
    }

    // مقداردهی تاریخ انتخاب‌شده از DatePicker
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val dateStr = "$year/${monthOfYear + 1}/$dayOfMonth"
        edtDate.setText(dateStr)
    }
}
