package com.example.appinterface

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.appinterface.Api.contacto.ContactCreateActivity
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.R

class MainActivity : BaseActivity() {

    private lateinit var btnFormulario: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCommonUI()
        btnFormulario = findViewById(R.id.btnFormulario)
        btnFormulario.setOnClickListener { abrirFormularioContacto() }
    }

    fun crearmostrarpersonas(view: View) {
        Toast.makeText(this, "Mostrar roles (ejecución)", Toast.LENGTH_SHORT).show()
    }

    private fun abrirFormularioContacto() {
        startActivity(Intent(this, ContactCreateActivity::class.java))
    }

    private fun logout() {
        val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        recreate()
    }

    override fun isAdmin(): Boolean = true
}
