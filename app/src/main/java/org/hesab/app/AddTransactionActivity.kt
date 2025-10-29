package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import com.samanzamani.persiandate.PersianDate
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // وقتی روی دکمه تاریخ کلیک شد
        binding.btnPickDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // دیالوگ انتخاب تاریخ میلادی (سیستم اندروید)
        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            // تبدیل تاریخ میلادی انتخاب‌شده به شمسی
            val gregorian = Calendar.getInstance()
            gregorian.set(y, m, d)

            val persianDate = PersianDate(gregorian.time)
            selectedDate =
                "${persianDate.shYear}/${persianDate.shMonth}/${persianDate.shDay}"

            binding.edtDate.setText(selectedDate)
        }, year, month, day)

        datePicker.show()
    }
}
