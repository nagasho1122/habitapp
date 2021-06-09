package com.nagase.nagasho.myapplayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_all_data_view.*

class allDataView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_data_view)

        // create dummy data
        val goalofData = arrayOf("筋肉バキバキ","怪獣の鼻唄")
        val targetofData= arrayOf("ダイエット","バウンディ")
        val habitnumberofData = arrayOf("9","3")
        val resultofData= arrayOf("success","failure")

        // use a linear layout manager
        my_recycler_view.layoutManager = LinearLayoutManager(this)

        // set adapter
        my_recycler_view.adapter = RecyclerAdapter(goalofData,targetofData,habitnumberofData,resultofData)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        my_recycler_view.setHasFixedSize(true)
    }
}