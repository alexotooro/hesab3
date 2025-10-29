package samanzamani.persiandate

class PersianDateFormat {
    fun format(date: PersianDate): String {
        val y = date.shYear
        val m = date.shMonth.toString().padStart(2, '0')
        val d = date.shDay.toString().padStart(2, '0')
        return "$y/$m/$d"
    }
}
