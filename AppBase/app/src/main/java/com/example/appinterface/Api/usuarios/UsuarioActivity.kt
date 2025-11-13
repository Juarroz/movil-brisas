package com.example.appinterface.Api.usuarios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.R
import com.example.appinterface.core.BaseActivity
import com.example.appinterface.core.RetrofitInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioActivity : BaseActivity(), UsuarioAdapter.Listener {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private lateinit var fabCrear: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        initCommonUI()

        rvUsuarios = findViewById(R.id.rvUsuarios)
        fabCrear = findViewById(R.id.fabCrearUsuario)

        // Recycler horizontal
        rvUsuarios.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = UsuarioAdapter(mutableListOf(), this)
        rvUsuarios.adapter = adapter

        fabCrear.setOnClickListener {
            // Inflar vista del diálogo
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_user, null)
            val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
            val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
            val etTelefono = dialogView.findViewById<EditText>(R.id.etTelefono)
            val etDocnum = dialogView.findViewById<EditText>(R.id.etDocnum)
            val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelCreate)
            val btnCreate = dialogView.findViewById<Button>(R.id.btnCreateUser)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            btnCancel.setOnClickListener { dialog.dismiss() }

            btnCreate.setOnClickListener {
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val telefono = etTelefono.text.toString().trim().ifEmpty { null }
                val docnum = etDocnum.text.toString().trim().ifEmpty { null }

                // Validaciones mínimas
                if (nombre.isEmpty()) {
                    etNombre.error = "Requerido"
                    etNombre.requestFocus()
                    return@setOnClickListener
                }
                if (correo.isEmpty()) {
                    etCorreo.error = "Requerido"
                    etCorreo.requestFocus()
                    return@setOnClickListener
                }

                // Construir DTO (ajusta rolId/tipdocId si lo necesitas)
                val nuevo = UsuarioRequestDTO(
                    nombre = nombre,
                    correo = correo,
                    telefono = telefono,
                    password = "ClaveSegura123", // si tu API requiere password
                    docnum = docnum,
                    rolId = 1,   // por defecto 'usuario'; cambia si quieres spinner
                    tipdocId = 1,
                    origen = "registro",
                    activo = true
                )

                // Llamada Retrofit
                btnCreate.isEnabled = false
                RetrofitInstance.api2kotlin.createUsuario(nuevo)
                    .enqueue(object : Callback<UsuarioResponseDTO> {
                        override fun onResponse(call: Call<UsuarioResponseDTO>, response: Response<UsuarioResponseDTO>) {
                            btnCreate.isEnabled = true
                            if (response.isSuccessful) {
                                val created = response.body()
                                if (created != null) {
                                    // insertar en la lista local y refrescar Recycler
                                    adapter.addItem(created)
                                    rvUsuarios.scrollToPosition(0)
                                    Toast.makeText(this@UsuarioActivity, "Usuario creado", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                } else {
                                    Toast.makeText(this@UsuarioActivity, "Respuesta vacía", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@UsuarioActivity, "Error servidor: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<UsuarioResponseDTO>, t: Throwable) {
                            btnCreate.isEnabled = true
                            Toast.makeText(this@UsuarioActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            dialog.show()
        }

        // cargar datos al inicio
        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        RetrofitInstance.api2kotlin.getUsuarios()
            .enqueue(object : Callback<PageWrapperDTO> {
                override fun onResponse(call: Call<PageWrapperDTO>, response: Response<PageWrapperDTO>) {
                    if (response.isSuccessful) {
                        // 1. Obtiene el cuerpo del objeto PageWrapperDTO
                        val wrapper = response.body()

                        // 2. Extrae la lista del campo 'content'
                        val lista = wrapper?.content ?: emptyList()

                        adapter.updateList(lista)
                        findViewById<View>(R.id.tvEmpty).visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
                    } else {
                        Toast.makeText(this@UsuarioActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                // ¡LÍNEA CORREGIDA! Cambiar List<UsuarioResponseDTO> por PageWrapperDTO
                override fun onFailure(call: Call<PageWrapperDTO>, t: Throwable) {
                    Toast.makeText(this@UsuarioActivity, "Fallo: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // --- UserAdapter.Listener implementation ---

    override fun onToggleActivo(user: UsuarioResponseDTO, position: Int) {
        val nuevoEstado = !user.activo
        RetrofitInstance.api2kotlin.cambiarEstadoUsuario(user.id, nuevoEstado)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // actualizar UI localmente
                        val updated = user.copy(activo = nuevoEstado)
                        adapter.updateItem(position, updated)
                        Toast.makeText(this@UsuarioActivity, if (nuevoEstado) "Usuario activado" else "Usuario desactivado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UsuarioActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@UsuarioActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onDelete(user: UsuarioResponseDTO, position: Int) {
        RetrofitInstance.api2kotlin.deleteUsuario(user.id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        // eliminar localmente
                        adapter.removeItem(position)
                        Toast.makeText(this@UsuarioActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UsuarioActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@UsuarioActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onChangeRole(user: UsuarioResponseDTO, position: Int) {
        // Aquí deberías abrir un diálogo o una nueva actividad para seleccionar un nuevo rol.
        // Necesitarás una lista de roles (si es que la tienes) y una llamada a la API.

        // Ejemplo de placeholder (debes implementar la lógica de la UI y la API)
        Toast.makeText(this, "Abrir diálogo para cambiar rol de: ${user.nombre}", Toast.LENGTH_LONG).show()

        // **Ejemplo de lógica a implementar (requiere API call):**
        /*
        val newRoleId = 2 // Ejemplo: obtener del diálogo
        RetrofitInstance.api2kotlin.cambiarRolUsuario(user.id, newRoleId)
            .enqueue(object : Callback<UsuarioResponseDTO> {
                override fun onResponse(call: Call<UsuarioResponseDTO>, response: Response<UsuarioResponseDTO>) {
                    if (response.isSuccessful) {
                        val updatedUser = response.body()
                        if (updatedUser != null) {
                            adapter.updateItem(position, updatedUser)
                            Toast.makeText(this@UsuarioActivity, "Rol de ${updatedUser.nombre} cambiado a ${updatedUser.rolNombre}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UsuarioActivity, "Error al cambiar rol: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UsuarioResponseDTO>, t: Throwable) {
                     Toast.makeText(this@UsuarioActivity, "Fallo al cambiar rol: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        */
    }

    // Forzar admin durante pruebas
    override fun isAdmin(): Boolean = true
}
