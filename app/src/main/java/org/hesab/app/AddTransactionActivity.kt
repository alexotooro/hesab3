package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import com.github.samanzamani.persiandate.PersianDate
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📅 نمونه‌سازی از PersianDate
        val persianDate = PersianDate()
        val today = "${persianDate.shYear}/${persianDate.shMonth}/${persianDate.shDay}"

        binding.dateText.text = today
    }
}
