package com.nagase.nagasho.myapplayout

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_setting.view.*
import kotlin.toString as toString1

class setting : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        targetText.isEnabled=false
        goalText.isEnabled=false
        frequencyText.isEnabled=false
        durationText.isEnabled=false


        val data: Data? = read()

        if (data != null) {
            targetText.hint = data.target
            goalText.hint = data.goal
            frequencyText.hint = data.frequency.toString()
            durationText.hint = data.duration.toString()
        }

        editButton.setOnClickListener{ //編集の処理
            AlertDialog.Builder(this)
                .setTitle("編集しますか？")
                .setMessage("今までの記録を保持したまま変更できます。")
                .setNegativeButton("いいえ") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("はい") { dialog, which ->
                    // Respond to positive button press
                    texteditable()
                    decideButton.isEnabled=true
                    decideButton.background=getDrawable(R.drawable.background_circle_enable)
                }
                .show()
        }

        decideButton.setOnClickListener{
            val goal: String = goalText.text.toString1()
            val target: String = targetText.text.toString1()
            val frequency: Int = frequencyText.text.toString().toInt()
            val duration: Int = durationText.text.toString().toInt()
            save(goal, target, frequency, duration)
            println(data)
        }





        topAppBar.setNavigationOnClickListener {
            finish()
            // Handle navigation icon press
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
    fun read(): Data? {
        return realm.where(Data::class.java).findFirst()
    }

    fun save(goal: String, target: String, frequency: Int,duration: Int){
        val data: Data? = read()

        realm.executeTransaction { //データベースへの書き込み

            if (data != null) {
                data.goal = goal
                data.target = target
                data.frequency = frequency
                data.duration = duration
            } else {
                val newdata: Data = it.createObject(Data::class.java) //保存するデータの新規作成
                newdata.goal = goal
                newdata.target = target
                newdata.frequency = frequency
                newdata.duration = duration
            }

            Snackbar.make(container,"編集しました", Snackbar.LENGTH_SHORT).show() //表示する長さ等の設定
        }
    }
    private fun texteditable(){
        targetText.isEnabled= true
        goalText.isEnabled= true
        frequencyText.isEnabled= true
        durationText.isEnabled= true
    }
}