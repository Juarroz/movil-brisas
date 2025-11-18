package com.example.appinterface.Api.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.MainActivity
import com.example.appinterface.R


class LoginActivity : AppCompatActivity() {

    private lateinit var etUser: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnMockLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUser = findViewById(R.id.et_username)
        etPass = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnMockLogin = findViewById(R.id.btn_mock_login)

        // Botón "real" (si implementas retrofit más adelante)
        btnLogin.setOnClickListener {
            Toast.makeText(this, "Pendiente integrar backend (usa Mock para probar)", Toast.LENGTH_SHORT).show()
            // cuando integres: llamar RetrofitInstance.api2kotlin.login(...)
        }

        // BOTÓN MOCK: guarda sesion localmente para validar UI sin backend
        btnMockLogin.setOnClickListener {
            val username = etUser.text.toString().ifBlank { "demo.user" }
            val rolesSet = setOf("USER") // cambia a setOf("ADMIN") para probar menú admin
            val prefs = getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE).edit()
            prefs.putString("username", username)
            prefs.putStringSet("roles", rolesSet)
            prefs.apply()
            Toast.makeText(this, "Sesión mock iniciada como $username", Toast.LENGTH_SHORT).show()
            // Regresa a Main y limpia backstack
            val i = Intent(this@LoginActivity, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
    }

    // firma auxiliar si usas onClick en xml
    fun onBackToMain(view: View) {
        finish()
    }
}