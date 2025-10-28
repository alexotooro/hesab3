پروژه اندروید (آفلاین) — مخصوص Android 7 (minSdkVersion = 24)

محتوای این پوشه:
- build.gradle (project-level)
- settings.gradle
- gradle.properties
- app/
  - src/main/AndroidManifest.xml
  - src/main/java/org/hesab/app/MainActivity.kt
  - src/main/res/layout/activity_main.xml
  - src/main/assets/index.html
  - src/main/assets/style.css
  - src/main/assets/script.js

چطور در GitHub قرار بدیم و APK بسازیم (بدون نیاز به Android Studio):
1) یک مخزن جدید در GitHub بساز.
2) فایل‌ها (کل محتوا) را در ریشهٔ مخزن آپلود کن (Add file → Upload files).
3) به مسیر .github/workflows/ فایل build-apk.yml را بساز و محتوا را پیست کن.
4) Commit کن. سپس به تب Actions برو و workflow را اجرا کن یا منتظر push خودکار باش.
5) بعد از اتمام اجرا، در صفحهٔ action مربوطه بخش Artifacts، فایل APK را دانلود کن.

نکته: این workflow از بستهٔ gradle سیستم (apt-get install gradle) استفاده می‌کند و نیازی به gradlew ندارد.
