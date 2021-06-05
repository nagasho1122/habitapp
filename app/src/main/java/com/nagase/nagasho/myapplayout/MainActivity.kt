package com.nagase.nagasho.myapplayout

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.new_app_widget.*
import net.soft.vrg.flexiblecalendar.CalendarDay
import net.soft.vrg.flexiblecalendar.FlexibleCalendarView
import net.soft.vrg.flexiblecalendar.calendar_listeners.FlexibleCalendarMonthCallback
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity(),  FlexibleCalendarMonthCallback {

    //------calendarsetting------
    private lateinit var flexibleCalendarView: FlexibleCalendarView
    private val df = SimpleDateFormat("yyyy年-MMMM", Locale.getDefault())
    //------calendarsettingfinish------
    val realm: Realm = Realm.getDefaultInstance()
    var habitdates = mutableListOf<Int>()



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //------calendarsetting------
        flexibleCalendarView = findViewById(R.id.calendarWidget)
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
        val doneaction = Intent(this, doneaction::class.java)
        var todaydate: LocalDate = LocalDate.now()
        var realmtodaysdata:RealmResults<allData>? = realm.where<allData>()
                    .equalTo("date", todaydate.toString())
                    .findAll()
        var realmhabitdata:RealmResults<allData>? = realm.where<allData>()
                    .findAll()
        var sortdata = realmhabitdata?.sort("id",Sort.DESCENDING)
        var frequencycheking:frequencycheck? = readfrequencydata()


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

        if(realmhabitdata.isNullOrEmpty() == false && sortdata.isNullOrEmpty() == false) {
            var latesthabitdata = sortdata[0]

            //------calendar add image-------
            for (habitdata in realmhabitdata) {
                var achievedate = LocalDate.parse(habitdata.date, DateTimeFormatter.ISO_DATE)
                var period = ChronoUnit.DAYS.between(todaydate, achievedate).toInt()
                habitdates.add(period)
            }


            //今日のデータがあるとdoneボタン無効。習慣頻度に合わせてdoneボタン無効
            if (realmtodaysdata?.size != 0) {
                doneButtonswitch(false)
            } else {
                if (Math.abs((ChronoUnit.DAYS.between(LocalDate.parse(latesthabitdata?.date, DateTimeFormatter.ISO_DATE), todaydate).toInt()) ) < (data?.frequent.toString().toInt())) {
                    savecheck(false)
                    updateWidget()
                    doneButtonswitch(false)
                } else {
                    savecheck(true)
                    doneButtonswitch(true)
                }
            }
        }

        //回数出力  回数データがnullか0であればdoneボタン有効
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
            var achieven = false
            if(datedata != null) {
                var nownum : Int =datedata.habitnumber
                nownum++
                savenumber(nownum)
                habitNumber.text=nownum.toString()
                if(data!!.duration.toInt() == nownum){
                    achieven = true
                }
            }else{
                var nownum : Int =0
                nownum++
                savenumber(nownum)
                habitNumber.text=nownum.toString()
            }
            var today: LocalDate = LocalDate.now()
            insertData(data!!.goal, data!!.target, data!!.frequent, data!!.duration, today.toString())
            doneButtonswitch(false)
            updateWidget()
            doneaction.putExtra("achieven",achieven)
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
    fun readfrequencydata(): frequencycheck? {
        return realm.where(frequencycheck::class.java).findFirst()
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
    fun savecheck(bool: Boolean){
        val frequencychecking: frequencycheck? = readfrequencydata()

        realm.executeTransaction {
            //データベースへの書き込み
            if (frequencychecking != null) {
                frequencychecking.check = bool
            } else {
                val newdata: frequencycheck = it.createObject(frequencycheck::class.java) //保存するデータの新規作成
                newdata.check = bool
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
    }fun updateWidget(){
        val intent = Intent(this,NewAppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(applicationContext)
                .getAppWidgetIds(ComponentName(applicationContext,NewAppWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids)
        sendBroadcast(intent)
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