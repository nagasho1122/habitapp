package com.nagase.nagasho.myapplayout

import android.graphics.Color
import android.graphics.Color.BLACK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.toString as toString1

class setting : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        nottexteditable()

        val goal = intent.getStringExtra("goal")
        val target = intent.getStringExtra("target")
        val frequent = intent.getStringExtra("frequency")
        val duration = intent.getStringExtra("duration")


        if (goal != "0") {
            targetText.setText(target)
            goalText.setText(goal)
            frequencyText.setText(frequent)
            durationText.setText(duration)
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
            var editgoal = goalText.text.toString1()
            var edittarget=targetText.text.toString1()
            var editfrequent=frequencyText.text.toString()
            var editduration = durationText.text.toString()
            save(editgoal, edittarget, editfrequent, editduration)
            nottexteditable()
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

    fun save(goal: String, target: String, frequent: String,duration: String){
        val data: Data? = read()

        realm.executeTransaction {
            //データベースへの書き込み
            if (data != null) {
                data.goal = goal
                data.target = target
                data.frequent = frequent
                data.duration = duration
            } else {
                val newdata: Data = it.createObject(Data::class.java) //保存するデータの新規作成
                newdata.goal = goal
                newdata.target = target
                newdata.frequent = frequent
                newdata.duration = duration
            }

            Snackbar.make(buttons,"編集しました", Snackbar.LENGTH_SHORT).show() //表示する長さ等の設定
        }
    }
    private fun texteditable(){
        targetText.isEnabled= true
        goalText.isEnabled= true
        frequencyText.isEnabled= true
        durationText.isEnabled= true
    }
    private fun nottexteditable(){
        targetText.isEnabled= false
        goalText.isEnabled= false
        frequencyText.isEnabled= false
        durationText.isEnabled= false
        targetText.setTextColor(Color.BLACK)
        goalText.setTextColor(Color.BLACK)
        frequencyText.setTextColor(Color.BLACK)
        durationText.setTextColor(Color.BLACK)
    }
}