package com.example.appinterface.Api.usuarios.data

import com.google.gson.annotations.SerializedName

data class EmpleadoDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rolNombre") val rolNombre: String?

)