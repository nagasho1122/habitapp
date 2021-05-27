package com.nagase.nagasho.myapplayout

import io.realm.RealmObject

open class Data (
    open var goal: String = "",
    open var target: String ="",
    open var frequency: Int = 0,
    open var duration: Int = 0
) : RealmObject()