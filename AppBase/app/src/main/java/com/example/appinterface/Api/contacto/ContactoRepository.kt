package com.example.appinterface.Api.contacto

import com.example.appinterface.core.RetrofitInstance
import retrofit2.Call

class ContactoRepository {

    fun enviarContacto(contacto: ContactoFormularioRequestDTO): Call<ContactoFormularioResponseDTO> {
        return RetrofitInstance.api2kotlin.enviarContacto(contacto)
    }

    fun listarContactos(): Call<List<ContactoFormularioResponseDTO>> {
        return RetrofitInstance.api2kotlin.listarContactos()
    }


    fun actualizarContacto(id: Int, update: ContactoFormularioUpdateDTO): Call<ContactoFormularioResponseDTO> {
        return RetrofitInstance.api2kotlin.actualizarContactoDTO(id, update)
    }

    fun eliminarContacto(id: Int): Call<Void> {
        return RetrofitInstance.api2kotlin.eliminarContacto(id) }
}

