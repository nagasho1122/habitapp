package com.nagase.nagasho.myapplayout


import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import net.soft.vrg.flexiblecalendar.CalendarDay
import net.soft.vrg.flexiblecalendar.CalendarDaySettings
import net.soft.vrg.flexiblecalendar.CustomCalendarDayCallback
import net.soft.vrg.flexiblecalendar.FlexibleCalendarView
import net.soft.vrg.flexiblecalendar.calendar_listeners.FlexibleCalendarCustomViewCallback
import java.util.*


class CustomDayUtils {

    fun getCustomizeDayView(context: Context,somedays:List<Int>): List<CalendarDay> {
        val vrCalendarDays = ArrayList<CalendarDay>()

        for (habitdata in somedays) {
            vrCalendarDays.add(getSomeDay(context, habitdata))
        }

        return vrCalendarDays
    }




    private fun getSomeDay(context: Context, num:Int): CalendarDay {//今日からnum日引いた日付の背景変更

        val someDay = CalendarDay()

        val someDaySettings = CalendarDaySettings()
        someDaySettings.dayTextColor = R.color.colornone
        someDay.calendarDaySettings = someDaySettings
        someDay.customViewCallback = object : CustomCalendarDayCallback {
            override fun getNewCustomiseView(): View {
                val imageView = ImageView(context)
                imageView.setImageResource(R.drawable.simple_ex)
                return imageView
            }
        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, num)
        someDay.date = cal.time
        someDay.calendarDaySettings = someDaySettings
        return someDay
    }


}