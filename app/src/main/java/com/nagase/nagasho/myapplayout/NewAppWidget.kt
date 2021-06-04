package com.nagase.nagasho.myapplayout

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import net.soft.vrg.flexiblecalendar.FlexibleCalendarView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    private lateinit var flexibleCalendarView: FlexibleCalendarView
    private val df = SimpleDateFormat("yyyy年-MMMM", Locale.getDefault())



    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.new_app_widget
            )


            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}