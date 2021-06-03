package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_doneaction.*

class doneaction : AppCompatActivity() {
    val realm: Realm = Realm.getDefaultInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doneaction)
        val back = Intent(this,MainActivity::class.java)
        val makenewhabit = Intent(this,setting::class.java)
        val achieven = intent.getBooleanExtra("achieven",false)


        if(achieven){
            commentText.text = """
                    |目標の期間を達成しました！
                    |おめでとうございます。
                    |新たな目標を設定しましょう！
                    """.trimMargin()
            backButton.text="設定"
            deleteData()
            deletedateData()
        }

        backButton.setOnClickListener {
            if(achieven){
                makenewhabit.putExtra("makenew",true)
                startActivity(makenewhabit)
                finish()
            }else {
                startActivity(back)
                finish()
            }
        }
    }
    fun read(): Data? {
        return realm.where(Data::class.java).findFirst()
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
}