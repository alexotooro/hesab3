package org.hesab.app

import samanzamani.persiandate.PersianDate
import samanzamani.persiandate.PersianDateFormat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📅 نمونه‌سازی از PersianDate
        val pd = PersianDate()
val pdFormat = PersianDateFormat()
val formattedDate = pdFormat.format(pd)
binding.dateText.text = today

    }
}
