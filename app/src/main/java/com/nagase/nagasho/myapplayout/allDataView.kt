package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_all_data_view.*
import kotlinx.android.synthetic.main.activity_all_data_view.topAppBar
import kotlinx.android.synthetic.main.activity_main.*

class allDataView : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_data_view)

        val mainintent = Intent(this, MainActivity::class.java)
        val settingintent = Intent(this, setting::class.java)
        val backstatus = intent.getStringExtra("backstatus")

        var carddatas: RealmResults<cardData>? = realm.where<cardData>()
                .findAll()



        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.helpicondatacheck -> {
                    tutorialdata()
                    // Handle favorite icon press
                    true

                }
                R.id.settingicondataview -> {
                    startActivity(settingintent)
                    true
                }
                else -> false
            }
        }

        topAppBar.setNavigationOnClickListener {
            if (backstatus == "main") {
                startActivity(mainintent)
                finish()
            } else if (backstatus == "seeting") {
                startActivity(settingintent)
                finish()
            }

            // Handle navigation icon press
        }

        // create dummy data
        val goalofData = mutableListOf<String>()
        val targetofData = mutableListOf<String>()
        val themeofData = mutableListOf<String>()
        val habitnumberofData = mutableListOf<String>()
        val resultofData = mutableListOf<String>()

        if (carddatas.isNullOrEmpty() == false) {
            for (data in carddatas) {
                goalofData.add(data.goal)
                targetofData.add(data.target)
                themeofData.add(data.theme)
                habitnumberofData.add(data.number)
                if(data.result=="success") {
                    resultofData.add("????????????")
                }else{
                    resultofData.add("??????..")
                }
            }
        }else {
            goalofData.add("????????????")
            targetofData.add("????????????")
            themeofData.add("????????????")
            habitnumberofData.add("????????????")
            resultofData.add("????????????")
            nothingtutorial()
        }

        goalofData.reverse()
        targetofData.reverse()
        themeofData.reverse()
        habitnumberofData.reverse()
        resultofData.reverse()

            // use a linear layout manager
            my_recycler_view.layoutManager = LinearLayoutManager(this)

            // set adapter
            my_recycler_view.adapter = RecyclerAdapter(goalofData, targetofData, themeofData, habitnumberofData, resultofData)

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            my_recycler_view.setHasFixedSize(true)
        }
    fun tutorialdata(){
        AlertDialog.Builder(this)
                .setTitle("??????????????????????????????????????????")
                .setMessage("""
                    |???????????????????????????????????????
                    |????????????????????????????????????
                    |??????????????????????????????
                    |?????????????????????????????????????????????
                    """.trimMargin())
                .setPositiveButton("?????????") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
    }
    fun nothingtutorial(){
        AlertDialog.Builder(this)
                .setTitle("????????????????????????")
                .setMessage("""
                    |???????????????????????????????????????
                    |?????????????????????
                    |?????????????????????????????????????????????
                    """.trimMargin())
                .setPositiveButton("?????????") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
    }
}