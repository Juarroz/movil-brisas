package com.example.appinterface.Api.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.appinterface.MainActivity
import com.example.appinterface.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LoginBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var etUser: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.login_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etUser = view.findViewById(R.id.et_username_sheet)
        etPass = view.findViewById(R.id.et_password_sheet)
        btnLogin = view.findViewById(R.id.btn_login_sheet)

        btnLogin.setOnClickListener {
            // Aquí integras tu validación real con backend.
            // Por ahora mostramos un toast simple y no guardamos sesión.
            val username = etUser.text.toString().ifBlank { "" }
            val password = etPass.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(requireContext(), "Por favor completa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si quieres mantener comportamiento mock (aceptar cualquier credencial),
            // reemplaza este bloque por guardado en prefs. Si no, muestra toast:
            Toast.makeText(requireContext(), "Pendiente integrar backend", Toast.LENGTH_SHORT).show()

            // Opcional: si quieres guardar sesión automáticamente (imitando mock), descomenta:
            // val prefs = requireActivity().getSharedPreferences("brisas_prefs", Context.MODE_PRIVATE).edit()
            // prefs.putString("username", username)
            // prefs.putStringSet("roles", setOf("USER"))
            // prefs.apply()
            // dismiss()
            // val i = Intent(requireActivity(), MainActivity::class.java)
            // i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            // startActivity(i)
        }
    }
}
