package com.nagase.nagasho.myapplayout

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import android.graphics.Color
import androidx.core.content.ContextCompat
import net.soft.vrg.flexiblecalendar.CalendarDay
import net.soft.vrg.flexiblecalendar.FlexibleCalendarView
import net.soft.vrg.flexiblecalendar.calendar_listeners.FlexibleCalendarMonthCallback
import net.soft.vrg.flexiblecalendar.calendar_listeners.OnCalendarClickListener
import net.soft.vrg.flexiblecalendar.calendar_listeners.OnCalendarLongClickListener
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity(),  FlexibleCalendarMonthCallback {

    //------calendarsetting------
    private lateinit var flexibleCalendarView: FlexibleCalendarView
    private val df = SimpleDateFormat("yyyy-MMMM", Locale.getDefault())
    //------calendarsettingfinish------
    val realm: Realm = Realm.getDefaultInstance()
    var habitdates = mutableListOf<Int>()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //realm.deleteAll()

        //------calendarsetting------
        flexibleCalendarView = findViewById(R.id.calendar)
        flexibleCalendarView.getSettings()
            .setCalendarMonthCallback(this)

        flexibleCalendarView.setDateFormat(df)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.TUESDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.WEDNESDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.FRIDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.SUNDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.MONDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.THURSDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.SATURDAY)
            .setTextColor(Color.BLACK)
        flexibleCalendarView.weekContainer()
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTitle))
        flexibleCalendarView.titleContainer()
            .setBackgroundColor(ContextCompat.getColor(this, R.color.colorTitle))
        //------calendarsettingfinish------



        //データの読み込み
        val data: Data? = read()
        var datedata: dateData? = readdate()
        val preview = Intent(this, setting::class.java)
        val doneaction = Intent(this,doneaction::class.java)
        var todaydate: LocalDate = LocalDate.now()
        var realmtodaysdata = realm.where<allData>()
            .equalTo("date", todaydate.toString())
            .findAll()
        var realmhabitdata = realm.where<allData>()
            .findAll()


        //------calendar add image-------
        for (habitdata in realmhabitdata){
            var achievedate = LocalDate.parse(habitdata.date, DateTimeFormatter.ISO_DATE)
            var period = ChronoUnit.DAYS.between(todaydate,achievedate).toInt()
            habitdates.add(period)
            println(habitdates)
        }

        if (realmtodaysdata.size != 0) {
            doneButtonswitch(false)
        } else {
            doneButtonswitch(true)
        }

        //データから目的と目標を出力
        if (data?.goal != null) {
            textView2.text = data.target
            textView3.text = data.goal
        } else {
            preview.putExtra("first", true)
            startActivity(preview)
            finish()
        }
        if (data?.goal == "") {
            preview.putExtra("first", true)
            startActivity(preview)
            finish()
        }

        //回数出力
        if (datedata != null ) {
            if(datedata.habitnumber ==0){
                doneButtonswitch(true)
            }
            habitNumber.text = datedata.habitnumber.toString()
        }else{
            savenumber(0)
            doneButtonswitch(true)
            habitNumber.text="0"
        }

        datedata=readdate()


        val calendarView = CalendarView(this)
        calendarView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)


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
            var today: LocalDate = LocalDate.now()
            insertData(data!!.goal, data!!.target, data!!.frequent, data!!.duration, today.toString())
            doneButtonswitch(false)
            startActivity(doneaction)
            finish()
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

    //------calendarsetting------
    override fun getCustomizeDayView(calendar: Calendar): List<CalendarDay> {

        val customDayUtils = CustomDayUtils()

        return customDayUtils.getCustomizeDayView(this,habitdates)
    }
    //------calendarsettingfinish------

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
    fun insertData(goal: String, target: String, frequent: String,duration: String,data:String){
        realm.executeTransaction{
            var id = realm.where<allData>().max("id")
            var nextId = (id?.toLong() ?:  0)+1
            var realmObject1 = realm.createObject<allData>(nextId)
                    realmObject1.date=data
                    realmObject1.goal=goal
                    realmObject1.target=target
                    realmObject1.duration=duration
                    realmObject1.frequent=frequent
        }
        Log.d("Realminsert","result:${realm.where<allData>().findAll()}")
    }
    private  fun doneButtonswitch(bool:Boolean){
        if(bool){
            doneButton.isEnabled=true
            doneButton.background=getDrawable(R.drawable.background_circle)
        }else{
            doneButton.isEnabled=false
            doneButton.background=getDrawable(R.drawable.background_circle5)
        }
    }
}