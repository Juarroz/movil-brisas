package com.example.appinterface.Api.productos

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R

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