package com.nagase.nagasho.myapplayout

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.topAppBar
import kotlinx.android.synthetic.main.activity_setting.*
import kotlin.toString as toString1

class setting : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()
    var data: Data? = read()
    var datedata: dateData? = readdate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        nottexteditable()

        var goal = data?.goal
        var target = data?.target
        var theme = data?.theme
        var frequent = data?.frequent
        var duration = data?.duration
        var first = intent.getBooleanExtra("first",false)
        var makenew = intent.getBooleanExtra("makenew",false)
        var state:String=""
        var realmhabitdata: RealmResults<allData>? = realm.where<allData>()
                .findAll()

        val mainintent = Intent(this,MainActivity::class.java)
        val checkdata = Intent(this,allDataView::class.java)

        editTextcolorset()

        if(first){
            texteditable()
            decideButtonswitch(true)
            failButtonswitch(false)
            achieveButtonswitch(false)
            editButtonswitch(false)
            state="習慣を設定しました。"
            tutorial()
        }else if(makenew){
            texteditable()
            decideButtonswitch(true)
            failButtonswitch(false)
            achieveButtonswitch(false)
            editButtonswitch(false)
            state="習慣を設定しました。"
            AlertDialog.Builder(this)
                    .setTitle("新たな習慣を設定しましょう")
                    .setMessage("""
                    |目標達成おめでとうございます。
                    |あなたは着実に習慣化の力が身についてきています。
                    |新たな習慣は前よりも厳しいものにしましょう！
                    |今回も頑張ってください！
                    """.trimMargin())
                    .setPositiveButton("新たな習慣を設定") { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
        }else if(data?.goal == null){ //データが無いとき目的の追加のみ行える。
            texteditable()
            decideButtonswitch(true)
            failButtonswitch(false)
            achieveButtonswitch(false)
            editButtonswitch(false)
            state="習慣を設定しました。"
            AlertDialog.Builder(this)
                    .setTitle("習慣を設定しましょう")
                    .setMessage("""
                    |目的、目標が登録されていません。
                    |新たな習慣を設定しましょう。
                    """.trimMargin())
                    .setPositiveButton("習慣を設定") { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
        }

        if (goal != null) {
            targetText.setText(target)
            goalText.setText(goal)
            themeEditText.setText(theme)
            frequencyText.setText(frequent)
            durationText.setText(duration)
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.helpicondatacheck -> {
                    tutorial()
                    // Handle favorite icon press
                    true

                }
                R.id.dataiconSetting -> {
                    checkdata.putExtra("backstatus","seeting")
                    startActivity(checkdata)
                    // Handle favorite icon press
                    true

                }
                else -> false
            }
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
            var edittheme= themeEditText.text.toString1()
            var editfrequent=frequencyText.text.toString()
            var editduration = durationText.text.toString()
            if(editgoal.isNullOrBlank() or edittarget.isNullOrBlank() or editfrequent.isNullOrBlank() or edittheme.isNullOrBlank() or editduration.isNullOrBlank()){
                AlertDialog.Builder(this)
                    .setTitle("空欄があります")
                    .setMessage("必要な項目をすべて入力してください。")
                    .setPositiveButton("戻る") { dialog, which ->
                        // Respond to positive button press
                    }
                    .show()
            }else {
                save(editgoal, edittarget, edittheme, editfrequent, editduration, state)
                nottexteditable()
                decideButtonswitch(false)
                failButtonswitch(true)
                achieveButtonswitch(true)
                editButtonswitch(true)
                updateWidget()
                data= read()
                editTextcolorset()
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
                    if(data?.goal != null) {
                        if(datedata?.habitnumber != null) {
                            savecarddata(goalText.text.toString1(),targetText.text.toString1(),themeEditText.text.toString1(),datedata!!.habitnumber.toString(),"faiure")
                        }
                        deleteData()
                    }
                    if(datedata?.habitnumber != null) {
                        deletedateData()
                    }
                    if(realmhabitdata.isNullOrEmpty() == false ) {
                        if((goal !=null) and (target!=null) and (theme != null)) {
                            deletechoicealldata(goal!!, target!!,theme!!)
                        }
                    }
                    textclean()
                    updateWidget()
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
                    if(datedata?.habitnumber != null) {
                        savecarddata(goalText.text.toString1(), targetText.text.toString1(), themeEditText.text.toString1(), datedata!!.habitnumber.toString(), "success")
                    }else {
                        savecarddata(goalText.text.toString1(), targetText.text.toString1(), themeEditText.text.toString1(), "0", "success")
                    }
                    texteditable()
                    textclean()
                    deleteData()
                    deletedateData()
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
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // InputMethodManager をキャストしながら取得
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // エルビス演算子でViewを取得できなければ return false
        // focusViewには入力しようとしているのEditTextが取得されるはず
        val focusView = currentFocus ?: return false

        // このメソッドでキーボードを閉じる
        inputMethodManager.hideSoftInputFromWindow(
            focusView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

        return false
    }
    fun read(): Data? {
        return realm.where(Data::class.java).findFirst()
    }

    fun readdate(): dateData? {
        return realm.where(dateData::class.java).findFirst()
    }
    fun readcarddata(): cardData? {
        return realm.where(cardData::class.java).findFirst()
    }
    fun save(goal: String, target: String, theme:String, frequent: String,duration: String,text: String){
        val data: Data? = read()

        realm.executeTransaction {
            //データベースへの書き込み
            if (data != null) {
                data.goal = goal
                data.target = target
                data.theme = theme
                data.frequent = frequent
                data.duration = duration
            } else {
                val newdata: Data = it.createObject(Data::class.java) //保存するデータの新規作成
                newdata.goal = goal
                newdata.target = target
                newdata.theme = theme
                newdata.frequent = frequent
                newdata.duration = duration
            }

            Snackbar.make(buttons,text, Snackbar.LENGTH_SHORT).show() //表示する長さ等の設定
        }
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
    fun deletechoicealldata(choicegoal:String,choicetarget:String,choicetheme:String){
        realm.beginTransaction()
        var target3 = realm.where<allData>()
                .equalTo("goal",choicegoal)
                .equalTo("target",choicetarget)
                .equalTo("theme",choicetheme)
                .findAll()
        target3.deleteAllFromRealm()
        realm.commitTransaction()
    }
    private fun texteditable(){
        targetText.isEnabled= true
        goalText.isEnabled= true
        themeEditText.isEnabled=true
        frequencyText.isEnabled= true
        durationText.isEnabled= true
    }
    private fun textclean(){
        targetText.setText("")
        goalText.setText("")
        themeEditText.setText("")
        frequencyText.setText("")
        durationText.setText("")
    }
    private fun nottexteditable(){
        targetText.isEnabled= false
        goalText.isEnabled= false
        themeEditText.isEnabled=false
        frequencyText.isEnabled= false
        durationText.isEnabled= false
        targetText.setTextColor(Color.BLACK)
        goalText.setTextColor(Color.BLACK)
        frequencyText.setTextColor(Color.BLACK)
        durationText.setTextColor(Color.BLACK)
    }fun updateWidget(){
        val intent = Intent(this,NewAppWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(applicationContext)
                .getAppWidgetIds(ComponentName(applicationContext,NewAppWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids)
        sendBroadcast(intent)
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
    private  fun editTextcolorset(){
        frequencyText.setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
        themeEditText.setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
        durationText.setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
        goalText.setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
        targetText.setTextColor((ContextCompat.getColor(this, R.color.changetextcolor)))
    }
    private fun tutorial(){
        AlertDialog.Builder(this)
                .setTitle("習慣を設定しましょう")
                .setMessage("""
                    |目的と目標を明確にすることでモチベーションを持続できます。
                    |目的と目標の違いは以下を参考にしてください。
                    |
                    |目的:健康になる
                    |目標:腹筋バキバキ
                    |テーマ：腹筋100回
                    """.trimMargin())
                .setPositiveButton("次へ") { dialog, which ->
                    // Respond to positive button press
                    AlertDialog.Builder(this)
                            .setTitle("習慣を設定しましょう")
                            .setMessage("""
                    |習慣頻度とは、あなたの習慣が何日ごとに行なわれるかを表します。
                    |毎日であれば1日、3日に一回であれば3日を入力します。
                    |目標回数は達成したい習慣の回数を書いてください。
                    |まずは多すぎない回数を設定すると良いでしょう。
                    """.trimMargin())
                            .setPositiveButton("習慣化を初める") { dialog, which ->
                                // Respond to positive button press
                            }
                            .show()
                }
                .show()
    }
}