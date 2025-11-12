package com.example.appinterface.Api.usuarios

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appinterface.R
import com.example.appinterface.core.RetrofitInstance
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsuarioActivity : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtTelefono: EditText
    private lateinit var txtDocnum: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCargar: Button
    private lateinit var btnEliminar: Button
    private lateinit var listaUsuarios: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        txtNombre = findViewById(R.id.txtNombre)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtTelefono = findViewById(R.id.txtTelefono)
        txtDocnum = findViewById(R.id.txtDocnum)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnCargar = findViewById(R.id.btnCargar)
        btnEliminar = findViewById(R.id.btnEliminar)
        listaUsuarios = findViewById(R.id.txtListaUsuarios)

        btnGuardar.setOnClickListener { crearUsuario() }
        btnCargar.setOnClickListener { cargarUsuarios() }
        btnEliminar.setOnClickListener { eliminarUsuario() }


        val topTabLayout = findViewById<com.google.android.material.tabs.TabLayout>(R.id.topTabLayout)
        val isAdmin = true  // cámbialo según el caso real

        if (isAdmin) {
            topTabLayout?.visibility = View.VISIBLE
            topTabLayout?.apply {
                removeAllTabs()
                addTab(newTab().setText(getString(R.string.tab_users)))
                addTab(newTab().setText(getString(R.string.tab_contacts)))
                addTab(newTab().setText(getString(R.string.tab_orders)))
                addTab(newTab().setText(getString(R.string.tab_custom)))
            }
        } else {
            topTabLayout?.visibility = View.GONE
        }
    }

    private fun crearUsuario() {
        val usuario = UsuarioRequestDTO(
            nombre = txtNombre.text.toString(),
            correo = txtCorreo.text.toString(),
            telefono = txtTelefono.text.toString(),
            password = "ClaveSegura123",
            docnum = txtDocnum.text.toString(),
            rolId = 1,
            tipdocId = 1
        )

        RetrofitInstance.api2kotlin.createUsuario(usuario)
            .enqueue(object : Callback<UsuarioResponseDTO> {
                override fun onResponse(
                    call: Call<UsuarioResponseDTO>,
                    response: Response<UsuarioResponseDTO>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UsuarioActivity, "Usuario creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@UsuarioActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UsuarioResponseDTO>, t: Throwable) {
                    Toast.makeText(this@UsuarioActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cargarUsuarios() {
        RetrofitInstance.api2kotlin.getUsuarios()
            .enqueue(object : Callback<List<UsuarioResponseDTO>> {
                override fun onResponse(
                    call: Call<List<UsuarioResponseDTO>>,
                    response: Response<List<UsuarioResponseDTO>>
                ) {
                    if (response.isSuccessful) {
                        val lista = response.body()?.joinToString("\n") { u -> "${u.id} - ${u.nombre}" }
                        listaUsuarios.text = lista
                    } else {
                        listaUsuarios.text = "Error al cargar usuarios"
                    }
                }

                override fun onFailure(call: Call<List<UsuarioResponseDTO>>, t: Throwable) {
                    listaUsuarios.text = "Fallo: ${t.message}"
                }
            })
    }

    private fun eliminarUsuario() {
        val id = txtDocnum.text.toString().toLongOrNull()
        if (id == null) {
            Toast.makeText(this, "Ingresa un ID válido en docnum", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitInstance.api2kotlin.deleteUsuario(id)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
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
}
