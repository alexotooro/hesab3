package org.hesab.app

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import java.lang.reflect.Field

/**
 * 🅰️ FontHelper
 *
 * وظیفه: اعمال فونت انتخاب‌شده توسط کاربر روی کل برنامه.
 * فونت‌ها باید در مسیر `assets/fonts/` با نام‌های زیر باشند:
 * - iransans.ttf
 * - vazir.ttf
 * - diana.ttf
 *
 * کلیدهای ذخیره‌شده در SharedPreferences:
 * - app_font  → system / iransans / vazir / diana
 * - font_name → (نسخه فارسی قدیمی)
 */
object FontHelper {
    private const val TAG = "FontHelper"

    /**
     * خواندن تنظیم فونت از SharedPreferences و اعمال فونت سراسری.
     * فراخوانی: معمولاً در `Application.onCreate()` یا ابتدای هر Activity.
     */
    fun applyFont(context: Context) {
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        val fontKey = defaultPrefs.getString(
            "app_font",
            legacyPrefs.getString("font_name", "vazir")
        ) ?: "vazir"

        val assetName = when (fontKey.lowercase()) {
            "سیستم", "system" -> null
            "ایران‌سنس", "iransans" -> "fonts/iransans.ttf"
            "وزیر", "vazir" -> "fonts/vazir.ttf"
            "دیانا", "diana" -> "fonts/diana.ttf"
            else -> "fonts/vazir.ttf"
        }

        if (assetName == null) {
            Log.i(TAG, "applyFont: using system default font")
            return
        }

        try {
            val tf = Typeface.createFromAsset(context.assets, assetName)
            replaceDefaultFont(tf)
            Log.i(TAG, "applyFont: custom font applied → $assetName")
        } catch (e: Exception) {
            Log.e(TAG, "applyFont: failed to load font $assetName", e)
        }
    }

    /**
     * جایگزین‌سازی فونت‌های پیش‌فرض Typeface (DEFAULT, BOLD, SANS_SERIF, ...).
     */
    private fun replaceDefaultFont(newTypeface: Typeface) {
        val staticFields = arrayOf(
            "DEFAULT",
            "DEFAULT_BOLD",
            "SANS_SERIF",
            "SERIF",
            "MONOSPACE"
        )
        for (fieldName in staticFields) {
            try {
                val field: Field = Typeface::class.java.getDeclaredField(fieldName)
                field.isAccessible = true
                field.set(null, newTypeface)
            } catch (t: Throwable) {
                Log.w(TAG, "replaceDefaultFont: couldn't replace $fieldName", t)
            }
        }
    }

    /**
     * 🔹 اعمال فونت روی کل ویو (برای زمانی که reflection کافی نیست)
     * مثال:
     * ```kotlin
     * FontHelper.applyToAll(findViewById(android.R.id.content))
     * ```
     */
    fun applyToAll(root: View) {
        when (root) {
            is ViewGroup -> traverseAndApply(root)
            is TextView -> applyToTextView(root)
        }
    }

    private fun traverseAndApply(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            when (child) {
                is ViewGroup -> traverseAndApply(child)
                is TextView -> applyToTextView(child)
            }
        }
    }

    private fun applyToTextView(tv: TextView) {
        try {
            val context = tv.context
            val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)
            val fontKey = defaultPrefs.getString("app_font", legacyPrefs.getString("font_name", "vazir")) ?: "vazir"

            val assetName = when (fontKey.lowercase()) {
                "سیستم", "system" -> null
                "ایران‌سنس", "iransans" -> "fonts/iransans.ttf"
                "وزیر", "vazir" -> "fonts/vazir.ttf"
                "دیانا", "diana" -> "fonts/diana.ttf"
                else -> "fonts/vazir.ttf"
            }

            if (assetName != null) {
                val tf = Typeface.createFromAsset(context.assets, assetName)
                tv.typeface = tf
            }
        } catch (e: Exception) {
            Log.w(TAG, "applyToTextView: failed", e)
        }
    }
}
