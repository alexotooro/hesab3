package org.hesab.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etCategory = findViewById<EditText>(R.id.etCategory)
        val etNote = findViewById<EditText>(R.id.etNote)
        val rbIncome = findViewById<RadioButton>(R.id.rbIncome)
        val rbExpense = findViewById<RadioButton>(R.id.rbExpense)
        val btnSave = findViewById<Button>(R.id.btnSave)

        val db = AppDatabase.getDatabase(this)
        val dao = db.transactionDao()

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString()
            if (amountText.isEmpty()) {
                Toast.makeText(this, "مبلغ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toLong()
            val category = etCategory.text.toString()
            val note = etNote.text.toString()
            val isIncome = rbIncome.isChecked

            val transaction = Transaction(
                amount = amount,
                category = category,
                note = note,
                isIncome = isIncome,
                date = System.currentTimeMillis()
            )

            CoroutineScope(Dispatchers.IO).launch {
                dao.insert(transaction)
                finish()
            }
        }
    }
}
