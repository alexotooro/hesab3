package org.hesab.app.sms

import java.util.Date

object PersianDateHelper {
    fun toPersianDateString(date: Date): String {
        // اینجا بسته به پکیج کلاس‌های PersianDate.java و PersianDateFormat.java که خودت داری،
        // نام پکیج را تنظیم کن.
        // مثال فرضی (اگر کلاس‌ها در پکیج com.example.persian باشند):
        // val pd = com.example.persian.PersianDate(date.time)
        // val fmt = com.example.persian.PersianDateFormat("yyyy/MM/dd")
        // return fmt.format(pd)
        // در صورت نبودن، به‌صورت fallback تاریخ میلادی ساده برمی‌گردد:
        val cal = java.util.Calendar.getInstance()
        cal.time = date
        val y = cal.get(java.util.Calendar.YEAR)
        val m = cal.get(java.util.Calendar.MONTH) + 1
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d/%02d/%02d", y, m, d)
    }
}
