package com.example.appinterface

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class
ProductosActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_productos)
        }

    fun volverpag(v: View) {
        onBackPressed()
    }

}