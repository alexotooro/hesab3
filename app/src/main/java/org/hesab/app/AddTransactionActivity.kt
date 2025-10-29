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

        binding.btnPickDate.setOnClickListener { showDatePicker() }

        binding.btnSave.setOnClickListener {
            val amount = binding.edtAmount.text.toString()
            val desc = binding.edtDescription.text.toString()
            val date = binding.edtDate.text.toString()

            // فعلاً فقط تست خروجی کنسول (بعداً ذخیره توی دیتابیس)
            println("✅ مبلغ: $amount | توضیح: $desc | تاریخ: $date")
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(this, { _, y, m, d ->
            val gregorian = Calendar.getInstance()
            gregorian.set(y, m, d)
            val persianDate = PersianDate(gregorian.time)
            selectedDate = "${persianDate.shYear}/${persianDate.shMonth}/${persianDate.shDay}"
            binding.edtDate.setText(selectedDate)
        }, year, month, day)

        picker.show()
    }
}
