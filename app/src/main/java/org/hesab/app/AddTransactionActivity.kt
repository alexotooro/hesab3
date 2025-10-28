package org.hesab.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var radioExpense: RadioButton
    private lateinit var radioIncome: RadioButton
    private lateinit var editTextAmount: EditText
    private lateinit var buttonSave: Button
    private var transactionType: String = "expense"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        radioExpense = findViewById(R.id.radioExpense)
        radioIncome = findViewById(R.id.radioIncome)
        editTextAmount = findViewById(R.id.editTextAmount)
        buttonSave = findViewById(R.id.buttonSave)

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

        buttonSave.setOnClickListener {
            val typeText = if (transactionType == "expense") "هزینه" else "درآمد"
            val amount = editTextAmount.text.toString()
            Toast.makeText(this, "$typeText با مبلغ $amount ثبت شد", Toast.LENGTH_SHORT).show()
        }
    }
}
