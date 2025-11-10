package com.example.appinterface.Api.auth

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val tv = findViewById<TextView>(R.id.tv_profile_info)
        tv.text = "Perfil (pendiente implementar)."
    }
}