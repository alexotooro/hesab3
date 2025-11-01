package org.hesab.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.hesab.app.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAbout.text = "برنامه حساب\nنسخه ۱.۰\nتوسعه‌دهنده: Aleco"
        FontHelper.refreshFont(binding.root)
    }
}
