package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.CalendarView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarView = CalendarView(this)
        calendarView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // 親レイアウトに、CalendarViewを追加
        val linearLayout = findViewById<LinearLayout>(R.id.container)
        linearLayout.addView(calendarView)

        val preview = Intent(this,setting::class.java)

        doneButton.setOnClickListener{
            startActivity(preview)
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingicon -> {
                    startActivity(preview)
                    // Handle favorite icon press
                    true

                }
                else -> false
            }
        }
    }

}