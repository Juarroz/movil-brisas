package com.example.appinterface.core

import android.content.Context
import com.example.appinterface.Api.pedidos.data.data.SessionManager
import com.example.appinterface.core.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://dog.ceo/api/"
    private const val BASE_URL_APIKOTLIN = "http://10.0.2.2:8080/api/"

    // Variable para guardar el contexto y crear SessionManager
    private var appContext: Context? = null

    /**
     * Inicializa RetrofitInstance con el contexto de la aplicación
     * DEBE llamarse desde Application.onCreate() o antes del primer uso
     */
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Cliente OkHttp con interceptor de autenticación y logging
     */
    private val okHttpClient: OkHttpClient by lazy {
        val context = appContext ?: throw IllegalStateException(
            "RetrofitInstance no ha sido inicializado. Llama a init(context) primero."
        )

        val sessionManager = SessionManager(context)
        val authInterceptor = AuthInterceptor(sessionManager)

        // Interceptor para logging (útil para debugging)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * API de tu backend (CON interceptor de autenticación)
     */
    val api2kotlin: ApiServicesKotlin by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_APIKOTLIN)
            .client(okHttpClient) // Usa el cliente con AuthInterceptor
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServicesKotlin::class.java)
    }

    /**
     * Obtiene una instancia de SessionManager
     */
    fun getSessionManager(): SessionManager {
        val context = appContext ?: throw IllegalStateException(
            "RetrofitInstance no ha sido inicializado. Llama a init(context) primero."
        )
        return SessionManager(context)
    }
}