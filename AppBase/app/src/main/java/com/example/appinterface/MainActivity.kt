package com.example.appinterface

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.annotation.SuppressLint
import android.content.Intent
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appinterface.Adapter.PersonaAdapter
import com.example.appinterface.Api.DataResponse
import com.example.appinterface.Api.RetrofitInstance
import com.example.appinterface.Api.RolResponseDTO


class MainActivity : AppCompatActivity() {
    private lateinit var persona: Persona

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        val buttonGoToSecondActivity: Button = findViewById(R.id.buttonSegundaActividad)
        buttonGoToSecondActivity.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }
    }


    fun crearpersona(v: View) {

        var nombre = findViewById<EditText>(R.id.nombre)

        var edad = findViewById<EditText>(R.id.edad)

        persona = Persona()

        if (!nombre.text.isNullOrEmpty() && !edad.text.isNullOrEmpty())
            persona.Persona(nombre.text.toString(), edad.text.toString().toInt())

        var imgpersona = findViewById<ImageView>(R.id.imageView)
        // imgpersona.setImageResource(R.mipmap.marquez)

        RetrofitInstance.api.getHoundImages().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                if (response.isSuccessful) {
                    val images = response.body()?.message
                    if (!images.isNullOrEmpty()) {

                        Picasso.get().load(images[2]).into(imgpersona)
                    }
                } else {
                    Toast.makeText(applicationContext, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })

        var ppersona = findViewById<TextView>(R.id.textView)
        DataPersona(ppersona, persona)

    }

    private fun DataPersona(ppersona: TextView, persona: Persona) {
        var description: String = ""

        description += "Nombre " + persona.getNombre() + " "
        description += "Edad " + persona.getEdad()

        ppersona.text = description

    }

    fun crearmostrarpersonas(v: View) {
        val recyclerView = findViewById<RecyclerView>(R.id.RecyPersonas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        RetrofitInstance.api2kotlin.getRoles().enqueue(object : Callback<List<RolResponseDTO>> {
            override fun onResponse(
                call: Call<List<RolResponseDTO>>,
                response: Response<List<RolResponseDTO>>
            ) {
                if (response.isSuccessful) {
                    val roles = response.body()
                    if (roles != null && roles.isNotEmpty()) {
                        // Tomamos solo los nombres de los roles
                        val nombres = roles.map { it.nombre }
                        val adapter = PersonaAdapter(nombres)
                        recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this@MainActivity, "No hay roles disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RolResponseDTO>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error en la conexión con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }

}

