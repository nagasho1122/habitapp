package com.nagase.nagasho.myapplayout

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.BLACK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.delete
import io.realm.log.RealmLog.error
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.toString as toString1

class setting : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()
    val data: Data? = read()
    val datedata: dateData? = readdate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        nottexteditable()

        var goal = data?.goal
        var target = data?.target
        var frequent = data?.frequent
        var duration = data?.duration
        var first = intent.getBooleanExtra("first",false)
        var state:String=""


        val mainintent = Intent(this,MainActivity::class.java)

        if(first){
            texteditable()
            decideButtonswitch(true)
            failButtonswitch(false)
            achieveButtonswitch(false)
            editButtonswitch(false)
            state="習慣を設定しました。"
            AlertDialog.Builder(this)
                .setTitle("習慣を設定しましょう")
                .setMessage("""
                    |目的と目標を明確にすることでモチベーションを持続できます。
                    |習慣頻度と期間を選択することで記録を可視化できます。
                    |目的と目標の違いは以下を参考にしてください。
                    |目的:健康になる
                    |目標:腹筋バキバキ
                    """.trimMargin())
                .setPositiveButton("習慣化を初める") { dialog, which ->
                    // Respond to positive button press
                }
                .show()
        }

        if (goal != null) {
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
                    decideButtonswitch(true)
                    failButtonswitch(false)
                    achieveButtonswitch(false)
                    editButtonswitch(false)
                    state="編集されました。"
                }
                .show()
        }

        decideButton.setOnClickListener{
            var editgoal = goalText.text.toString1()
            var edittarget=targetText.text.toString1()
            var editfrequent=frequencyText.text.toString()
            var editduration = durationText.text.toString()
            if(editgoal.isNullOrBlank() or edittarget.isNullOrBlank() or editfrequent.isNullOrBlank() or editduration.isNullOrBlank()){
                AlertDialog.Builder(this)
                    .setTitle("空欄があります")
                    .setMessage("必要な項目をすべて入力してください。")
                    .setPositiveButton("戻る") { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
            }else {
                save(editgoal, edittarget, editfrequent, editduration, state)
                nottexteditable()
                decideButtonswitch(false)
                failButtonswitch(true)
                achieveButtonswitch(true)
                editButtonswitch(true)
            }
        }

        failButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("失敗しましたか？")
                .setMessage("記録が消去され、新たに各項目を設定します。")
                .setNegativeButton("いいえ") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("はい") { dialog, which ->
                    // Respond to positive button press
                    texteditable()
                    decideButtonswitch(true)
                    failButtonswitch(false)
                    achieveButtonswitch(false)
                    editButtonswitch(false)
                    state="新たな習慣を設定しました。今度こそ頑張ろう！"
                    realm.beginTransaction()
                    realm.deleteAll()
                    realm.commitTransaction()
                    targetText.setText("")
                    goalText.setText("")
                    frequencyText.setText("")
                    durationText.setText("")
                }
                .show()
        }

        achieveButton.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("目標を達成しましたか？")
                .setMessage("記録を保持し、新たな習慣を設定しましょう！")
                .setNegativeButton("いいえ") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("はい") { dialog, which ->
                    // Respond to positive button press
                    texteditable()
                    decideButtonswitch(true)
                    failButtonswitch(false)
                    achieveButtonswitch(false)
                    editButtonswitch(false)
                    state="新たな習慣を設定しました。今回も頑張ろう！"
                }
                .show()
        }


        topAppBar.setNavigationOnClickListener {
            startActivity(mainintent)
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

    fun readdate(): dateData? {
        return realm.where(dateData::class.java).findFirst()
    }

    fun save(goal: String, target: String, frequent: String,duration: String,text: String){
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

            Snackbar.make(buttons,text, Snackbar.LENGTH_SHORT).show() //表示する長さ等の設定
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
    private fun failButtonswitch(bool:Boolean){
        if(bool){
            failButton.isEnabled=true
            failButton.background=getDrawable(R.drawable.background_circle3)
        }else{
            failButton.isEnabled=false
            failButton.background=getDrawable(R.drawable.background_circle5)
        }
    }
    private  fun achieveButtonswitch(bool:Boolean){
        if(bool){
            achieveButton.isEnabled=true
            achieveButton.background=getDrawable(R.drawable.background_circle)
        }else{
            achieveButton.isEnabled=false
            achieveButton.background=getDrawable(R.drawable.background_circle5)
        }
    }
    private  fun decideButtonswitch(bool:Boolean){
        if(bool){
            decideButton.isEnabled=true
            decideButton.background=getDrawable(R.drawable.background_circle_enable)
        }else{
            decideButton.isEnabled=false
            decideButton.background=getDrawable(R.drawable.background_circle5)
        }
    }
    private fun editButtonswitch(bool:Boolean){
        if(bool){
            editButton.isEnabled=true
            editButton.background=getDrawable(R.drawable.background_circle2)
        }else{
            editButton.isEnabled=false
            editButton.background=getDrawable(R.drawable.background_circle5)
        }
    }
}