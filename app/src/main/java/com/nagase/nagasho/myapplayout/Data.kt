package com.nagase.nagasho.myapplayout

import io.realm.RealmObject

open class Data (
    open var goal: String = "",
    open var target: String ="",
    open var frequent: String = "",
    open var duration: String =""
) : RealmObject()