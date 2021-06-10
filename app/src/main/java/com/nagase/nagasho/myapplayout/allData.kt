package com.nagase.nagasho.myapplayout

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.time.LocalDate

open class allData : RealmObject() {//データ出力時に使うクラス。Doneボタンを押したときに保存するところ
    @PrimaryKey
    var id: Int = 0
    @Required
    var target: String = ""
    var goal: String =""
    var theme: String=""
    var frequent: String =""
    var duration: String =""
    var date: String=""
}