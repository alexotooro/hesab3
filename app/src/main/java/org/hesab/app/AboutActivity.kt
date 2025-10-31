package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        title = "درباره برنامه"

        val tvAbout = findViewById<TextView>(R.id.tvAbout)
        tvAbout.text = """
            برنامه حساب 🧾
            
            نسخه: 1.0.0
            
            توسعه‌دهنده: Aleco
            
            این برنامه برای مدیریت تراکنش‌های مالی طراحی شده است.
            امکانات:
            • افزودن و ویرایش تراکنش‌ها
            • ثبت خودکار تراکنش از پیامک‌های بانکی
            • نمایش مانده حساب
            • رابط کاربری ساده و روان
            
            © 2025
        """.trimIndent()
    }
}
