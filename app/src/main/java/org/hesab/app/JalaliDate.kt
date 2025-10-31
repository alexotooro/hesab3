package org.hesab.app

import java.util.*

object JalaliDate {

    // تبدیل تاریخ میلادی امروز به شمسی (ساده و سبک برای اندروید 7)
    fun today(): String {
        val cal = Calendar.getInstance()
        val gYear = cal.get(Calendar.YEAR)
        val gMonth = cal.get(Calendar.MONTH) + 1
        val gDay = cal.get(Calendar.DAY_OF_MONTH)
        val jalali = toJalali(gYear, gMonth, gDay)
        return "${jalali.first}/${jalali.second}/${jalali.third}"
    }

    private fun toJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val g_d_m = intArrayOf(0, 31, if (gy % 4 == 0) 29 else 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        var gy2 = gy - 1600
        var gm2 = gm - 1
        var gd2 = gd - 1
        var g_day_no = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
        for (i in 0 until gm2) g_day_no += g_d_m[i + 1]
        g_day_no += gd2
        var j_day_no = g_day_no - 79
        val j_np = j_day_no / 12053
        j_day_no %= 12053
        var jy = 979 + 33 * j_np + 4 * (j_day_no / 1461)
        j_day_no %= 1461
        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365
            j_day_no = (j_day_no - 1) % 365
        }
        val j_days_in_month = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        var jm = 0
        while (jm < 11 && j_day_no >= j_days_in_month[jm]) {
            j_day_no -= j_days_in_month[jm]
            jm++
        }
        val jd = j_day_no + 1
        return Triple(jy, jm + 1, jd)
    }
}
