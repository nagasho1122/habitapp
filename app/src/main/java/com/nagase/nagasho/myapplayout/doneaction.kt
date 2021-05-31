package com.nagase.nagasho.myapplayout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_doneaction.*

class doneaction : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doneaction)
        val back = Intent(this,MainActivity::class.java)

        backButton.setOnClickListener {
            startActivity(back)
            finish()
        }
    }
}