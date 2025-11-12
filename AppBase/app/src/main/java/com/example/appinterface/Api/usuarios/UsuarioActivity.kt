package com.example.appinterface.Api.usuarios

import android.os.Bundle
import android.view.View
import android.widget.Toast
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
            // abrir pantalla de creación (placeholder)
            Toast.makeText(this, "Abrir crear usuario (pendiente)", Toast.LENGTH_SHORT).show()
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

    override fun onEdit(user: UsuarioResponseDTO, position: Int) {
        // placeholder: abrir pantalla de edición
        Toast.makeText(this, "Editar usuario: ${user.nombre}", Toast.LENGTH_SHORT).show()
        // aquí puedes lanzar UserEditActivity con extras
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

    override fun onViewHistory(user: UsuarioResponseDTO, position: Int) {
        // placeholder: abrir modal/detail
        Toast.makeText(this, "Ver historial: ${user.nombre}", Toast.LENGTH_SHORT).show()
    }

    // Forzar admin durante pruebas
    override fun isAdmin(): Boolean = true
}
