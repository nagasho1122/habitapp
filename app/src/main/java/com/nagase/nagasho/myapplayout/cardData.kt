package com.nagase.nagasho.myapplayout

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

open class cardData: RealmObject(){//データ出力時に使うクラス。Doneボタンを押したときに保存するところ
    @PrimaryKey
    var id: Int = 0
    @Required
    var goal: String = ""
    var target: String =""
    var theme: String=""
    var number: String =""
    var result: String =""
}