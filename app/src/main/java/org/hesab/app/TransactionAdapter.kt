<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="8dp"
    android:gravity="center_vertical">

    <TextView
        android:id="@+id/txt_date"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="1403/07/01" />

    <TextView
        android:id="@+id/txt_amount"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="50000" />

    <TextView
        android:id="@+id/txt_reason"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="خوراک" />

    <TextView
        android:id="@+id/txt_note"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="-" />

    <ImageView
        android:id="@+id/menu_icon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_more_vert"
        android:contentDescription="menu" />
</LinearLayout>
