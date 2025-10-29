package org.hesab.app

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAddTransactionBinding
import com.aliab.persiandatepicker.Listener
import com.aliab.persiandatepicker.PersianDatePickerDialog
import com.aliab.persiandatepicker.utils.PersianCalendar
import android.widget.Toast

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // دکمه انتخاب تاریخ
        binding.btnPickDate.setOnClickListener {
            showPersianDatePicker()
        }

        // دکمه ثبت تراکنش
        binding.btnSaveTransaction.setOnClickListener {
            val amount = binding.etAmount.text.toString()
            val description = binding.etDescription.text.toString()

            if (amount.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "لطفاً مبلغ و تاریخ را وارد کنید", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "تراکنش ثبت شد:\nمبلغ: $amount\nتاریخ: $selectedDate\nتوضیح: $description",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showPersianDatePicker() {
        val today = PersianCalendar()
        PersianDatePickerDialog(this)
            .setPositiveButtonString("تأیید")
            .setNegativeButton("انصراف")
            .setTodayButton("امروز")
            .setTodayButtonVisible(true)
            .setInitDate(today)
            .setMinYear(1400)
            .setMaxYear(1450)
            .setActionTextColor(resources.getColor(android.R.color.holo_blue_dark))
            .setListener(object : Listener {
                override fun onDateSelected(persianCalendar: PersianCalendar) {
                    selectedDate = persianCalendar.persianShortDate
                    binding.tvSelectedDate.text = selectedDate
                }

                override fun onDismissed() {
                    // کار خاصی لازم نیست انجام شود
                }
            }).show()
    }
}
