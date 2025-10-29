package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        // 📅 نمایش تاریخ امروز شمسی
        val persianDate = PersianDate()
        val persianDateFormat = PersianDateFormat()
        val today = persianDateFormat.format(persianDate)
        binding.etDate.setText(today)

        // 📆 انتخاب تاریخ با DatePicker
        binding.btnDatePicker.setOnClickListener {
            showPersianDatePicker()
        }

        // 🎯 دکمه ثبت تراکنش
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }

        // ❌ دکمه انصراف
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showPersianDatePicker() {
        val persianDate = PersianDate()
        val cal = Calendar.getInstance()
        cal.set(persianDate.shYear, persianDate.shMonth - 1, persianDate.shDay)

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$year/${month + 1}/$dayOfMonth"
                binding.etDate.setText(selectedDate)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveTransaction() {
        val date = binding.etDate.text.toString()
        val amount = binding.etAmount.text.toString()
        val title = binding.etTitle.text.toString()
        val description = binding.etDescription.text.toString()

        // ✅ تعیین نوع تراکنش از رادیوباتن‌ها
        val transactionType = if (binding.rbExpense.isChecked) "هزینه" else "درآمد"

        // 🧩 فعلاً نمایش در Logcat برای تست
        Log.d("AddTransaction", "نوع: $transactionType | مبلغ: $amount | بابت: $title | تاریخ: $date | توضیح: $description")

        // می‌تونی در آینده اینجا ذخیره در دیتابیس یا لیست اصلی رو اضافه کنی
        Toast.makeText(this, "تراکنش با موفقیت ثبت شد", Toast.LENGTH_SHORT).show()
        finish()
    }
}
