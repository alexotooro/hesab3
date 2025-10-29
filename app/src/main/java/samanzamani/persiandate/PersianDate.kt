package samanzamani.persiandate

import java.util.*

class PersianDate {
    private val gregorianCalendar = Calendar.getInstance()

    private val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

    var shYear: Int
    var shMonth: Int
    var shDay: Int

    init {
        val gy = gregorianCalendar.get(Calendar.YEAR)
        val gm = gregorianCalendar.get(Calendar.MONTH) + 1
        val gd = gregorianCalendar.get(Calendar.DAY_OF_MONTH)
        val gDayNo: Int
        var jDayNo: Int
        var jNp: Int

        var i: Int

        var gy2 = gy - 1600
        var gm2 = gm - 1
        var gd2 = gd - 1

        gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

        for (i in 0 until gm2)
            gDayNo += gDaysInMonth[i]
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0)))
            gDayNo++

        gDayNo += gd2

        jDayNo = gDayNo - 79

        jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        i = 0
        while (i < 11 && jDayNo >= jDaysInMonth[i]) {
            jDayNo -= jDaysInMonth[i]
            i++
        }

        shYear = jy
        shMonth = i + 1
        shDay = jDayNo + 1
    }
}
