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
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_setting.*
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
                .setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.WEDNESDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.FRIDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.SUNDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.MONDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.THURSDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.getDayOfWeekTextView(FlexibleCalendarView.SATURDAY)
                .setTextColor(ContextCompat.getColor(this, R.color.changetextcolor))
        flexibleCalendarView.weekContainer()
                .setBackgroundColor(ContextCompat.getColor(this, R.color.changebackground))
        flexibleCalendarView.titleContainer()
                .setBackgroundColor(ContextCompat.getColor(this, R.color.changebackground))
        //------calendarsettingfinish------


        //データの読み込み
        val data: Data? = read()
        var datedata: dateData? = readdate()
        val preview = Intent(this, setting::class.java)
        val doneaction = Intent(this, doneaction::class.java)
        val checkdata = Intent(this,allDataView::class.java)
        var todaydate: LocalDate = LocalDate.now()
        var realmtodaysdata:RealmResults<allData>? = realm.where<allData>()
                    .equalTo("date", todaydate.toString())
                    .findAll()
        var realmhabitdata:RealmResults<allData>? = realm.where<allData>()
                    .findAll()
        var sortdata = realmhabitdata?.sort("id",Sort.DESCENDING)
        var frequencycheking:frequencycheck? = readfrequencydata()
        var sharedPref = getSharedPreferences("firstcheck", Context.MODE_PRIVATE)
        var firstlychecking = sharedPref.getBoolean("bool",true)

        //widget更新
        updateWidget()


        //データから目的と目標を出力 データがあればDoneボタン押せるようになる
        if (data?.goal != null) {
            textView2.text = data.target
            textView3.text = data.goal
            themedatacardText.text=data.theme
            doneButtonswitch(true)
        } else {
            textView2.text ="目的を設定しよう"
            textView3.text = "目標を設定しよう"
            themedatacardText.text="テーマを設定しよう"
            doneButtonswitch(false)
        }

        //初回起動時処理
        if (firstlychecking) {
            AlertDialog.Builder(this)
                    .setTitle("習慣化を始めましょう")
                    .setMessage("""
                    |ダウンロードありがとうございます。
                    |本アプリはあなたの習慣化を手助けします。
                    |目標を達成した日は画面中央のDoneボタンを押すことでカレンダー上にスタンプが押されます。
                    |習慣を継続し、スタンプを増やしていきましょう！
                    """.trimMargin())
                    .setPositiveButton("次へ") { dialog, which ->
                        // Respond to positive button press
                        AlertDialog.Builder(this)
                                .setTitle("チュートリアル")
                                .setMessage("""
                    |初めに目標、目的等を設定する画面に移ります。
                    |設定画面は右上の歯車アイコンからも飛ぶことができます。
                    |ウィジェット機能も搭載しております。是非ご使用ください！
                    """.trimMargin())
                                .setPositiveButton("習慣化を初める") { dialog, which ->
                                    // Respond to positive button press
                                    sharedPref.edit().putBoolean("bool",false).apply()
                                    preview.putExtra("first", true)
                                    startActivity(preview)
                                }
                                .show()
                    }
                    .show()
        }

        //スタンプを押すデータを取得
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
                if(data?.frequent != null) {
                    if (Math.abs((ChronoUnit.DAYS.between(LocalDate.parse(latesthabitdata?.date, DateTimeFormatter.ISO_DATE), todaydate).toInt())) < (data?.frequent.toString().toInt())) {
                        savecheck(false)
                        updateWidget()
                        doneButtonswitch(false)
                    } else {
                        savecheck(true)
                        if (data?.goal != null) {
                            doneButtonswitch(true)
                        }
                    }
                }else{
                    doneButtonswitch(false)
                }
            }
        }

        //回数出力  回数データがnullか0であればdoneボタン有効
        if (datedata != null ) {
            habitNumber.text = datedata.habitnumber.toString()
        }else{
            habitNumber.text="0"
            if(data?.goal != null){
                doneButtonswitch(true)
            }
        }


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
                    savecarddata(data!!.goal, data!!.target, data!!.theme,nownum.toString(),"success")
                }
            }else{
                var nownum : Int =0
                nownum++
                savenumber(nownum)
                habitNumber.text=nownum.toString()
                if(data!!.duration.toInt() == nownum){
                    achieven = true
                    savecarddata(data!!.goal, data!!.target, data!!.theme,nownum.toString(),"success")
                }
            }
            var today: LocalDate = LocalDate.now()
            insertData(data!!.goal, data!!.target, data!!.theme, data!!.frequent, data!!.duration, today.toString())
            doneButtonswitch(false)
            updateWidget()
            doneaction.putExtra("achieven",achieven)
            startActivity(doneaction)
        }
        //Appbarの設定ページへ飛ぶ処理
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.settingicon -> {
                    startActivity(preview)
                    // Handle favorite icon press
                    true

                }
                R.id.dataicon -> {
                    checkdata.putExtra("backstatus","main")
                    startActivity(checkdata)
                    // Handle favorite icon press
                    true

                }
                R.id.helpicon -> {
                    AlertDialog.Builder(this)
                            .setTitle("ヘルプ")
                            .setMessage("""
                    |本アプリはあなたの習慣化を手助けします。
                    |現在の画面がホーム画面であり、右上の歯車アイコンを押すと目標等の設定ができます。
                    |目標を達成した日は画面中央のDoneボタンを押すことでカレンダー上にスタンプが押されます。
                    |習慣を継続し、スタンプを増やしていきましょう！
                    """.trimMargin())
                            .setPositiveButton("次へ") { dialog, which ->
                                // Respond to positive button press
                                AlertDialog.Builder(this)
                                        .setTitle("ウィジェット機能について")
                                        .setMessage("""
                    |ウィジェットでは目的、目標及び今日を含めた過去6日間の記録が見れます。
                    |目標を達成した日はウィジェット上のDoneボタンを押すことでもスタンプが押されます。
                    |アプリとウィジェットを駆使し、習慣化していきましょう！
                    """.trimMargin())
                                        .setPositiveButton("閉じる") { dialog, which ->
                                            // Respond to positive button press
                                        }
                                        .show()
                            }
                            .show()
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
    fun readcarddata(): cardData? {
        return realm.where(cardData::class.java).findFirst()
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
    fun insertData(goal: String, target: String, theme: String, frequent: String,duration: String,data:String){
        realm.executeTransaction{
            var id = realm.where<allData>().max("id")
            var nextId = (id?.toLong() ?:  0)+1
            var realmObject1 = realm.createObject<allData>(nextId)
                    realmObject1.date=data
                    realmObject1.goal=goal
                    realmObject1.target=target
                    realmObject1.theme=theme
                    realmObject1.duration=duration
                    realmObject1.frequent=frequent
        }
        Log.d("Realminsert","result:${realm.where<allData>().findAll()}")
    }
    fun savecarddata(goal: String, target: String, theme:String, number: String,result: String){
        realm.executeTransaction{
            var id = realm.where<cardData>().max("id")
            var nextId = (id?.toLong() ?:  0)+1
            var realmObject1 = realm.createObject<cardData>(nextId)
            realmObject1.goal=goal
            realmObject1.target=target
            realmObject1.theme=theme
            realmObject1.number=number
            realmObject1.result=result
        }
    }
    fun updateWidget(){
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