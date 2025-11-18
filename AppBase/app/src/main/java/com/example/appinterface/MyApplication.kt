package com.example.appinterface

import android.app.Application
import com.example.appinterface.core.RetrofitInstance


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializar RetrofitInstance con el contexto de la aplicación
        // Esto solo se ejecuta UNA VEZ en toda la vida de la app
        RetrofitInstance.init(this)

        // Aquí puedes inicializar otras cosas globales:
        // - Firebase
        // - Analytics
        // - Crashlytics
        // - Room Database
        // - etc.
    }
}