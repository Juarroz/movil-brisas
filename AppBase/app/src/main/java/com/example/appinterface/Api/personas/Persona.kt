package com.example.appinterface.Api.personas

class Persona( private var Nombre: String = "john",
               private var Edad: Int = 24, ) {


    fun Persona(nom: String, eda: Int){
        this.Nombre = nom
        this.Edad = eda
    }

    fun getNombre(): String{return Nombre }
    fun getEdad(): Int{return Edad }

}