package org.hesab.app

import android.content.Context
import android.graphics.Typeface
import android.widget.TextView

object FontHelper {

    fun applyFont(context: Context) {
        val prefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)
        val fontName = prefs.getString("font_name", "سیستم")

        when (fontName) {
            "سیستم" -> context.setTheme(R.style.Font_System)
            "ایران‌سنس" -> context.setTheme(R.style.Font_IranSans)
            "وزیر" -> context.setTheme(R.style.Font_Vazir)
            "دیانا" -> context.setTheme(R.style.Font_Diana)
        }
    }

    fun applyTo(textView: TextView, context: Context) {
        val prefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)
        val fontName = prefs.getString("font_name", "سیستم")

        val typeface = when (fontName) {
            "ایران‌سنس" -> Typeface.createFromAsset(context.assets, "fonts/iransans.ttf")
            "وزیر" -> Typeface.createFromAsset(context.assets, "fonts/vazir.ttf")
            "دیانا" -> Typeface.createFromAsset(context.assets, "fonts/diana.ttf")
            else -> Typeface.DEFAULT
        }

        textView.typeface = typeface
    }
}
