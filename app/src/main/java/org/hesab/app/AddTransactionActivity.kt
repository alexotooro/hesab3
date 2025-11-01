package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        db = AppDatabase.getInstance(this)
    }

    private fun saveTransaction(transaction: Transaction) {
        CoroutineScope(Dispatchers.IO).launch {
            db.transactionDao().insert(transaction)
        }
    }
}
