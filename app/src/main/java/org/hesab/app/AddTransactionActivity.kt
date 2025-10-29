package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import samanzamani.persiandate.PersianDate
import samanzamani.persiandate.PersianDateFormat

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📅 نمایش تاریخ امروز به‌صورت شمسی
        val persianDate = PersianDate()
        val persianDateFormat = PersianDateFormat()
        val today = persianDateFormat.format(persianDate)

        // اگر در layout ویوی مربوط به تاریخ داری، نام ID اون رو جایگزین کن
        binding.textDate.text = today
    }
}
