package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import samanzamani.persiandate.PersianDate
import samanzamani.persiandate.PersianDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📅 تاریخ امروز شمسی
        val persianDate = PersianDate()
        val persianDateFormat = PersianDateFormat()
        val today = persianDateFormat.format(persianDate)
        binding.etDate.setText(today)

        // 📆 دکمه انتخاب تاریخ
        binding.btnDatePicker.setOnClickListener {
            showPersianDatePicker()
        }
    }

    private fun showPersianDatePicker() {
        val persianDate = PersianDate()

        // 📅 تبدیل تاریخ شمسی به میلادی برای DatePicker
        val cal = Calendar.getInstance()
        cal.set(persianDate.shYear + 621, persianDate.shMonth - 1, persianDate.shDay)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // 🌀 تبدیل تاریخ میلادی انتخاب‌شده به شمسی
                val gregCal = Calendar.getInstance()
                gregCal.set(year, month, dayOfMonth)

                val newPersianDate = PersianDate()
                val persianDateFormat = PersianDateFormat()
                val formattedDate = persianDateFormat.format(newPersianDate)
                binding.etDate.setText(formattedDate)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}
