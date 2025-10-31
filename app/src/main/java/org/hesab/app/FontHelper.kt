package org.hesab.app

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.preference.PreferenceManager
import java.lang.reflect.Field

object FontHelper {
    private const val TAG = "FontHelper"

    /**
     * خواندن تنظیم فونت از SharedPreferences و اعمال جایگزینی فونت پیش‌فرض (با reflection).
     * کلیدها:
     * - app_font  (values: "system", "iransans", "vazir", "diana")
     * - font_name (legacy فارسی)
     *
     * فراخوانی: قبل از setContentView در Activity تا فونت کل برنامه جایگزین شود.
     *
     * توجه: نیاز است فایل‌های فونت در مسیر assets/fonts/ با نام‌های:
     *    iransans.ttf, vazir.ttf, diana.ttf
     * وجود داشته باشند.
     */
    fun applyFont(context: Context) {
        val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val legacyPrefs = context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)

        val fontKey = defaultPrefs.getString("app_font",
            legacyPrefs.getString("font_name", "vazir")
        ) ?: "vazir"

        val assetName = when (fontKey) {
            "سیستم", "system" -> null
            "ایران‌سنس", "iransans" -> "fonts/iransans.ttf"
            "وزیر", "vazir" -> "fonts/vazir.ttf"
            "دیانا", "diana" -> "fonts/diana.ttf"
            else -> "fonts/vazir.ttf"
        }

        if (assetName == null) {
            // سیستم پیش‌فرض: هیچ جایگزینی انجام نمی‌دهیم
            return
        }

        try {
            val tf = Typeface.createFromAsset(context.assets, assetName)
            replaceDefaultFont(tf)
        } catch (e: Exception) {
            Log.e(TAG, "applyFont: failed to load font $assetName", e)
        }
    }

    /**
     * جایگزین‌سازی فونت‌های ثابت کلاس Typeface برای اینکه تمام TextView ها فونت جدید را بگیرند.
     * این روش reflection روی فیلدهای Typeface انجام می‌دهد (DEFAULT, DEFAULT_BOLD, SANS_SERIF, SERIF, MONOSPACE).
     */
    private fun replaceDefaultFont(newTypeface: Typeface) {
        try {
            // فیلدهایی که معمولاً می‌خواهیم جایگزین شوند
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
        } catch (e: Exception) {
            Log.e(TAG, "replaceDefaultFont: error", e)
        }
    }

    /**
     * کمکی: اعمال فونت روی یک ViewGroup (در صورت نیاز برای مواردی که reflection کافی نیست).
     * مثال: FontHelper.applyToAll(rootView)
     */
    fun applyToAll(root: View) {
        if (root is ViewGroup) {
            traverseAndApply(root)
        } else if (root is TextView) {
            // اگر ریشه خودش TextView است
            applyToTextView(root)
        }
    }

    private fun traverseAndApply(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is ViewGroup) traverseAndApply(child)
            else if (child is TextView) applyToTextView(child)
        }
    }

    private fun applyToTextView(tv: TextView) {
        try {
            val defaultPrefs = PreferenceManager.getDefaultSharedPreferences(tv.context)
            val legacyPrefs = tv.context.getSharedPreferences("org.hesab.app_preferences", Context.MODE_PRIVATE)
            val fontKey = defaultPrefs.getString("app_font", legacyPrefs.getString("font_name", "vazir")) ?: "vazir"
            val assetName = when (fontKey) {
                "سیستم", "system" -> null
                "ایران‌سنس", "iransans" -> "fonts/iransans.ttf"
                "وزیر", "vazir" -> "fonts/vazir.ttf"
                "دیانا", "diana" -> "fonts/diana.ttf"
                else -> "fonts/vazir.ttf"
            }
            if (assetName != null) {
                val tf = Typeface.createFromAsset(tv.context.assets, assetName)
                tv.typeface = tf
            }
        } catch (e: Exception) {
            Log.w(TAG, "applyToTextView: failed", e)
        }
    }
}
