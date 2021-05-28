package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.LinearLayout
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //データの読み込み
        val data: Data? = read()

        if (data != null) {
            textView2.text = data.target
            textView3.text = data.goal
        }

        val calendarView = CalendarView(this)
        calendarView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // 親レイアウトに、CalendarViewを追加
        val linearLayout = findViewById<LinearLayout>(R.id.containerSetting)
        linearLayout.addView(calendarView)

        val preview = Intent(this,setting::class.java)

        doneButton.setOnClickListener{
            startActivity(preview)
        }
        //Appbarの設定ページへ飛ぶ処理
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingicon -> {
                    preview.putExtra("goal",data?.goal)
                    preview.putExtra("target",data?.target)
                    preview.putExtra("frequency",data?.frequent)
                    preview.putExtra("duration",data?.duration)
                    startActivity(preview)
                    // Handle favorite icon press
                    true

                }
                else -> false
            }
        }

    }
    //画面が閉じられるときにデータを保存
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    fun read(): Data? {
        return realm.where(Data::class.java).findFirst()
    }

}