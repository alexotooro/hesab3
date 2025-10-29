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

        // نمایش تاریخ در فیلد مربوطه
        binding.edtDate.setText(today)
    }
}
