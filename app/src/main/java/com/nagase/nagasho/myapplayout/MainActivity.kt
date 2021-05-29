package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*

class MainActivity : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //データの読み込み
        val data: Data? = read()
        var datedata: dateData? = readdate()
        val preview = Intent(this,setting::class.java)

        //データから目的と目標を出力
        if (data?.goal != null) {
            textView2.text = data.target
            textView3.text = data.goal
        }else{
            preview.putExtra("first",true)
            startActivity(preview)
            finish()
        }
        if(data?.goal == ""){
            preview.putExtra("first",true)
            startActivity(preview)
            finish()
        }
        //回数出力
        if(datedata != null){
            habitNumber.text = datedata.habitnumber.toString()
        }else{
            savenumber(0)

            habitNumber.text="0"
        }

        datedata=readdate()


        val calendarView = CalendarView(this)
        calendarView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // 親レイアウトに、CalendarViewを追加
        val linearLayout = findViewById<LinearLayout>(R.id.containerSetting)
        linearLayout.addView(calendarView)


        doneButton.setOnClickListener{
            if(datedata != null) {
                var nownum : Int =datedata.habitnumber
                nownum++
                savenumber(nownum)
                habitNumber.text=nownum.toString()
            }else{
                var nownum : Int =0
                nownum++
                savenumber(nownum)
                habitNumber.text=nownum.toString()
            }
        }
        //Appbarの設定ページへ飛ぶ処理
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingicon -> {
                    startActivity(preview)
                    finish()
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
    fun readdate(): dateData? {
        return realm.where(dateData::class.java).findFirst()
    }
    fun savenumber(number: Int){
        val datedata: dateData? = readdate()

        realm.executeTransaction {
            //データベースへの書き込み
            if (datedata != null) {
                datedata.habitnumber = number
            } else {
                val newdata: dateData = it.createObject(dateData::class.java) //保存するデータの新規作成
                newdata.habitnumber = number
            }
        }
    }
}