package org.hesab.app.sms

import java.util.Date

object PersianDateHelper {
    // فرض می‌کنیم کلاس‌های PersianDate / PersianDateFormat موجود و قابل استفاده‌اند
    fun toPersianDateString(date: Date): String {
        // کد جاوا/کلاس‌های شما ممکن است متد متفاوتی داشته باشند.
        // مثال فرضی - اگر متد دیگری داری، همین خط را با متد واقعی جایگزین کن.
        val pd = com.yourpackage.persian.PersianDate(date.time) // اگر نام پکیج فرق داره آن را تغییر بده
        val formatter = com.yourpackage.persian.PersianDateFormat("yyyy/MM/dd")
        return formatter.format(pd)
    }
}
