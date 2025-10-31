package org.hesab.app

import android.app.Activity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private var editingId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // 🔹 اعمال تم و فونت قبل از setContentView
        ThemeHelper.applyTheme(this)
        FontHelper.applyFont(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val editDate = findViewById<EditText>(R.id.editDate)
        val editAmount = findViewById<EditText>(R.id.editAmount)
        val editCategory = findViewById<EditText>(R.id.editCategory)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        val radioExpense = findViewById<RadioButton>(R.id.radioExpense)
        val radioIncome = findViewById<RadioButton>(R.id.radioIncome)
        val btnSave =
