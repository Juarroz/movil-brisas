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
import com.bumptech.glide.Glide
import com.example.appinterface.Api.CatResponseDTO
import com.example.appinterface.Api.RetrofitCataasClient


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

        val btnGatito: Button = findViewById(R.id.button)        // id del botón Gatito en tu XML
        val imgPersona: ImageView = findViewById(R.id.imageView) // id del ImageView en tu XML

        // Listener para cargar un gato cuando se presione el botón
        btnGatito.setOnClickListener {
            // Opcional: feedback inmediato
            Toast.makeText(this, "Solicitando gatito...", Toast.LENGTH_SHORT).show()
            mostrarGatito(imgPersona)
        }
    }




  /* fun crearpersona(v: View) {

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

    }*/

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

    private fun mostrarGatito(imgView: ImageView) {
        val btn = findViewById<Button>(R.id.button)
        btn.isEnabled = false
        btn.text = "Cargando..."

        RetrofitCataasClient.api.getRandomCat().enqueue(object : Callback<CatResponseDTO> {
            override fun onResponse(call: Call<CatResponseDTO>, response: Response<CatResponseDTO>) {
                btn.isEnabled = true
                btn.text = " Gatito"

                if (!response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    return
                }

                val cat = response.body()
                val urlFromApi = cat?.url

                if (urlFromApi.isNullOrBlank()) {
                    Toast.makeText(this@MainActivity, "Respuesta inválida del servidor", Toast.LENGTH_SHORT).show()
                    return
                }

                // Si la URL ya comienza con http/https usamos tal cual; si no, la completamos.
                val imageUrl = if (urlFromApi.startsWith("http://") || urlFromApi.startsWith("https://")) {
                    urlFromApi
                } else {
                    "https://cataas.com$urlFromApi"
                }

                // Log para depuración: revisa que la URL sea exactamente la que probaste en Postman
                android.util.Log.d("CATAAS", "imageUrl = $imageUrl")

                // Cargar la imagen con Glide
                Glide.with(this@MainActivity)
                    .load(imageUrl)
                    .centerCrop()
                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                    .error(android.R.drawable.stat_notify_error)
                    .into(imgView)
            }

            override fun onFailure(call: Call<CatResponseDTO>, t: Throwable) {
                btn.isEnabled = true
                btn.text = " Gatito"
                Toast.makeText(this@MainActivity, "Error de conexión: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                android.util.Log.e("CATAAS", "onFailure: ${t.localizedMessage}")
            }
        })
    }
}

