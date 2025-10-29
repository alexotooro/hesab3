package org.hesab.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aliab.persiandatepicker.PersianDatePickerDialog
import com.aliab.persiandatepicker.api.PersianPickerDate
import com.aliab.persiandatepicker.utils.PersianCalendar
import org.hesab.app.databinding.ActivityAddTransactionBinding

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 📅 دکمه انتخاب تاریخ شمسی
        binding.btnPickDate.setOnClickListener {
            val calendar = PersianCalendar()
            val datePicker = PersianDatePickerDialog(this)
                .setPositiveButtonString("تأیید")
                .setNegativeButton("انصراف")
                .setTodayButton("امروز")
                .setTodayButtonVisible(true)
                .setInitDate(calendar)
                .setActionTextColor("#5B86E5")
                .setListener(object : PersianDatePickerDialog.Listener {
                    override fun onDateSelected(persianPickerDate: PersianPickerDate) {
                        val dateStr = "${persianPickerDate.persianYear}/${persianPickerDate.persianMonth}/${persianPickerDate.persianDay}"
                        binding.edtDate.setText(dateStr)
                    }

                    override fun onDismissed() {
                        // هیچ کاری لازم نیست
                    }
                })
            datePicker.show()
        }

        // 💾 دکمه ثبت تراکنش
        binding.btnSave.setOnClickListener {
            val amount = binding.edtAmount.text.toString().trim()
            val desc = binding.edtDescription.text.toString().trim()
            val date = binding.edtDate.text.toString().trim()

            if (amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "لطفاً مبلغ و تاریخ را وارد کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // فعلاً فقط پیام نمایشی
            Toast.makeText(this, "تراکنش ثبت شد ✅", Toast.LENGTH_SHORT).show()
        }
    }
}
