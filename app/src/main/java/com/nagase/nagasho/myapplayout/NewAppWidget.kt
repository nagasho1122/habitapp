package com.nagase.nagasho.myapplayout

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import net.soft.vrg.flexiblecalendar.FlexibleCalendarView
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.math.absoluteValue

/**
 * Implementation of App Widget functionality.
 */
private const val DONE = "com.nagase.nagasho.myapplayout"

class NewAppWidget : AppWidgetProvider() {
    val realm: Realm = Realm.getDefaultInstance()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        val data: Data? = read()
        var datedata: dateData? = readdate()
        var realmhabitdata:RealmResults<allData>? = realm.where<allData>()
                .findAll()
        var sortdata = realmhabitdata?.sort("id", Sort.DESCENDING)
        var habitdates = mutableListOf<Int>()


        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,data,datedata,realmhabitdata,sortdata,habitdates)
        }
    }fun read(): Data? {
        return realm.where(Data::class.java).findFirst()
    }
    fun readdate(): dateData? {
        return realm.where(dateData::class.java).findFirst()
    }fun readfrequencydata(): frequencycheck? {
        return realm.where(frequencycheck::class.java).findFirst()
    }
    fun savenumber(number: Int){
        val datedata: dateData? = readdate()

        realm.executeTransaction {
            //????????????????????????????????????
            if (datedata != null) {
                datedata.habitnumber = number
            } else {
                val newdata: dateData = it.createObject(dateData::class.java) //????????????????????????????????????
                newdata.habitnumber = number
            }
        }
    }
    fun savecheck(bool: Boolean){
        val frequencychecking: frequencycheck? = readfrequencydata()

        realm.executeTransaction {
            //????????????????????????????????????
            if (frequencychecking != null) {
                frequencychecking.check = bool
            } else {
                val newdata: frequencycheck = it.createObject(frequencycheck::class.java) //????????????????????????????????????
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
    }
    fun deleteData(){
        realm.beginTransaction()
        var target = realm.where<Data>().findAll()
        target.deleteAllFromRealm()
        Log.d("RealmDelete", "deletePartRealm:${realm.where<Data>().findAll()}")
        realm.commitTransaction()
    }
    fun deletedateData(){
        realm.beginTransaction()
        var target2 = realm.where<dateData>().findAll()
        target2.deleteAllFromRealm()
        realm.commitTransaction()
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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (context == null || intent == null) return

        when (intent.action) {
            DONE -> {
                //?????????????????????????????????
                val data: Data? = read()
                var datedata: dateData? = readdate()
                val viewsome = RemoteViews(context.packageName, R.layout.new_app_widget)
                var todaydate: LocalDate = LocalDate.now()
                var realmtodaysdata:RealmResults<allData>? = realm.where<allData>()
                        .equalTo("date", todaydate.toString())
                        .findAll()
                var realmhabitdata:RealmResults<allData>? = realm.where<allData>()
                        .findAll()
                var sortdata = realmhabitdata?.sort("id",Sort.DESCENDING)
                savecheck(true)
                var frequencychecking:frequencycheck? = readfrequencydata()



                if(realmhabitdata.isNullOrEmpty() == false && sortdata.isNullOrEmpty() == false){
                    var latesthabitdata = sortdata[0]
                    if (Math.abs((ChronoUnit.DAYS.between(LocalDate.parse(latesthabitdata?.date, DateTimeFormatter.ISO_DATE), todaydate).toInt()) ) < (data?.frequent.toString().toInt())) {
                        savecheck(false)
                }



                if (realmtodaysdata?.size == 0 ){
                    if(frequencychecking?.check != false){
                        var achievecheck = false
                        if(datedata != null) {
                            var nownum : Int =datedata.habitnumber
                            nownum++
                            savenumber(nownum)
                            viewsome.setTextViewText(R.id.habitwidgetNumber,nownum.toString())
                            if(data!!.duration.toInt() <= nownum){
                                savecarddata(data!!.goal, data!!.target, data!!.theme,nownum.toString(),"success")
                                viewsome.setTextViewText(R.id.goalwidgetText,"???????????????????????????????????????")
                                viewsome.setTextViewText(R.id.themewidgetText,"???????????????????????????")
                                viewsome.setTextViewText(R.id.targetwidgetText,"???????????????????????????????????????")
                                achievecheck = true
                            }
                        }else{
                            var nownum : Int =0
                            nownum++
                            savenumber(nownum)
                            viewsome.setTextViewText(R.id.habitwidgetNumber,nownum.toString())
                            if(data!!.duration.toInt() <= nownum){
                                savecarddata(data!!.goal, data!!.target, data!!.theme,nownum.toString(),"success")
                                viewsome.setTextViewText(R.id.goalwidgetText,"???????????????????????????????????????")
                                viewsome.setTextViewText(R.id.themewidgetText,"???????????????????????????")
                                viewsome.setTextViewText(R.id.targetwidgetText,"???????????????????????????????????????")
                                achievecheck = true
                            }
                        }

                        var today: LocalDate = LocalDate.now()
                        insertData(data!!.goal, data!!.target, data!!.theme, data!!.frequent, data!!.duration, today.toString())
                        viewsome.setImageViewResource(R.id.stampView7,R.drawable.stamp1)
                        if(achievecheck){
                            deleteData()
                            deletedateData()
                        }
                    }
                }else if((datedata == null) and (data?.goal != null)){
                    var nownum : Int =0
                    var today: LocalDate = LocalDate.now()
                    nownum++
                    savenumber(nownum)
                    viewsome.setTextViewText(R.id.habitwidgetNumber,nownum.toString())
                    insertData(data!!.goal, data!!.target,data!!.theme,  data!!.frequent, data!!.duration, today.toString())
                    if(data!!.duration.toInt() <= nownum){
                        savecarddata(data!!.goal, data!!.target, data!!.theme,nownum.toString(),"success")
                        deletedateData()
                        deleteData()
                        viewsome.setTextViewText(R.id.goalwidgetText,"???????????????????????????????????????")
                        viewsome.setTextViewText(R.id.themewidgetText,"???????????????????????????")
                        viewsome.setTextViewText(R.id.targetwidgetText,"???????????????????????????????????????")
                    }
                    viewsome.setImageViewResource(R.id.stampView7,R.drawable.stamp1)
                }


                // ???????????????????????????
                val myWidget = ComponentName(context, NewAppWidget::class.java)
                val manager = AppWidgetManager.getInstance(context)
                manager.updateAppWidget(myWidget, viewsome)
            }
        }
    }



}

//onUpdate???appWidgetId???????????????????????????????????????
@RequiresApi(Build.VERSION_CODES.O)
internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        data: Data?,
        datedata:dateData?,
        realmhabitdata:RealmResults<allData>?,
        sortdata:RealmResults<allData>?,
        habitdates:MutableList<Int>
) {

    var todaydate: LocalDate = LocalDate.now()
    var period1 = Period.of(0, 0, 1)
    var period2 = Period.of(0, 0, 2)
    var period3 = Period.of(0, 0, 3)
    var period4 = Period.of(0, 0, 4)
    var period5 = Period.of(0, 0, 5)
    var period6 = Period.of(0, 0, 6)
    var formatter = DateTimeFormatter.ofPattern("M-dd")
    val viewsome = RemoteViews(context.packageName, R.layout.new_app_widget)

    var text1 = todaydate.format(formatter)
    var text2 = todaydate.minus(period1).format(formatter)
    var text3 = todaydate.minus(period2).format(formatter)
    var text4 = todaydate.minus(period3).format(formatter)
    var text5 = todaydate.minus(period4).format(formatter)
    var text6 = todaydate.minus(period5).format(formatter)
    var text7 = todaydate.minus(period6).format(formatter)

    viewsome.setTextViewText(R.id.dayText7, text1)
    viewsome.setTextViewText(R.id.dayText6, text2)
    viewsome.setTextViewText(R.id.dayText5, text3)
    viewsome.setTextViewText(R.id.dayText4, text4)
    viewsome.setTextViewText(R.id.dayText3, text5)
    viewsome.setTextViewText(R.id.dayText2, text6)
    viewsome.setTextViewText(R.id.dayText1, text7)

    if (realmhabitdata.isNullOrEmpty() == false) {
        for (habitdata in realmhabitdata) {
            var achievedate = LocalDate.parse(habitdata.date, DateTimeFormatter.ISO_DATE)
            var period = ChronoUnit.DAYS.between(todaydate, achievedate).toInt()
            if (period > -7) {
                habitdates.add(period)
            }
        }
    }

    if (habitdates.isNullOrEmpty() == false) {
        viewsome.setImageViewResource(R.id.stampView7, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView6, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView5, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView4, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView3, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView2, R.drawable.noimage)
        viewsome.setImageViewResource(R.id.stampView1, R.drawable.noimage)
        for (day in habitdates) {
            var newday = day * (-1)
            when (newday) {
                0 -> {
                    viewsome.setImageViewResource(R.id.stampView7, R.drawable.stamp1)
                }
                1 -> {
                    viewsome.setImageViewResource(R.id.stampView6, R.drawable.stamp1)
                }
                2 -> {
                    viewsome.setImageViewResource(R.id.stampView5, R.drawable.stamp1)
                }
                3 -> {
                    viewsome.setImageViewResource(R.id.stampView4, R.drawable.stamp1)
                }
                4 -> {
                    viewsome.setImageViewResource(R.id.stampView3, R.drawable.stamp1)
                }
                5 -> {
                    viewsome.setImageViewResource(R.id.stampView2, R.drawable.stamp1)
                }
                6 -> {
                    viewsome.setImageViewResource(R.id.stampView1, R.drawable.stamp1)
                }
            }

        }
    } else {
        viewsome.setImageViewResource(R.id.stampView7, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView6, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView5, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView4, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView3, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView2, R.drawable.line)
        viewsome.setImageViewResource(R.id.stampView1, R.drawable.line)

    }

    if (data?.goal != null) {
        viewsome.setTextViewText(R.id.goalwidgetText, data.goal)
    } else {
        viewsome.setTextViewText(R.id.goalwidgetText, "????????????????????????????????????")
    }
    if (data?.target != null) {
        viewsome.setTextViewText(R.id.targetwidgetText, data.target)
    } else {
        viewsome.setTextViewText(R.id.targetwidgetText, "????????????????????????????????????")
    }
    if (data?.theme != null) {
        viewsome.setTextViewText(R.id.themewidgetText, data.theme)
    } else {
        viewsome.setTextViewText(R.id.themewidgetText, "???????????????????????????")
    }
    if (datedata != null) {
        viewsome.setTextViewText(R.id.habitwidgetNumber, datedata.habitnumber.toString())
    } else {
        viewsome.setTextViewText(R.id.habitwidgetNumber, "0")
    }


    //Button??????????????????PendingIntent?????????????????????
    val doneIntent = Intent(context, NewAppWidget::class.java).apply { action = DONE }
    val donePendingIntent =
            PendingIntent.getBroadcast(context, 0, doneIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    viewsome.setOnClickPendingIntent(R.id.donewidgetButton, donePendingIntent)

    // ???????????????????????????
    appWidgetManager.updateAppWidget(appWidgetId, viewsome)
}
}