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
                resultofData.add(data.result)
            }
        }else {
            goalofData.add("記録なし")
            targetofData.add("記録なし")
            themeofData.add("記録なし")
            habitnumberofData.add("記録なし")
            resultofData.add("記録なし")
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
                .setTitle("過去のデータを参照できます。")
                .setMessage("""
                    |今まで習慣化に設定してきた
                    |習慣の各項目とその結果を
                    |見ることができます。
                    |今後の習慣化の糧にしましょう。
                    """.trimMargin())
                .setPositiveButton("閉じる") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
    }
    fun nothingtutorial(){
        AlertDialog.Builder(this)
                .setTitle("記録がありません")
                .setMessage("""
                    |過去の記録が存在しません。
                    |習慣を設定し、
                    |習慣化に挑戦していきましょう。
                    """.trimMargin())
                .setPositiveButton("閉じる") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
    }
}