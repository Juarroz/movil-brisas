package com.example.appinterface.Api.personalizacion

import android.util.Log
import com.example.appinterface.core.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository para manejar la lógica de negocio de personalización
 */
class PersonalizacionRepository {

    companion object {
        private const val TAG = "PersonalizacionRepo"
    }

    private val api = RetrofitInstance.api2kotlin

    /**
     * Obtiene todas las opciones de personalización
     * GET /api/opciones
     */
    suspend fun obtenerOpciones(): Result<List<PersonalizacionOption>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 Obteniendo opciones...")

            val response = api.obtenerOpciones()

            if (response.isSuccessful) {
                val opciones = response.body() ?: emptyList()
                Log.d(TAG, "✅ ${opciones.size} opciones obtenidas")

                opciones.forEach { opcion ->
                    Log.d(TAG, "  → ${opcion.nombre} (ID: ${opcion.id}, Clave: ${opcion.obtenerClave()})")
                }

                Result.success(opciones)
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al obtener opciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene los valores disponibles para una opción
     * GET /api/valores?opcId={id}
     */
    suspend fun obtenerValoresDisponibles(opcId: Int): Result<List<PersonalizacionValor>> =
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "🔄 Obteniendo valores para opción ID: $opcId")

                val response = api.obtenerValores(opcionId = opcId)

                if (response.isSuccessful) {
                    val valores = response.body() ?: emptyList()
                    Log.d(TAG, "✅ ${valores.size} valores obtenidos")

                    valores.forEach { valor ->
                        Log.d(TAG, "  → ${valor.nombre} (Slug: ${valor.obtenerSlug()})")
                    }

                    Result.success(valores)
                } else {
                    val error = "Error ${response.code()}: ${response.message()}"
                    Log.e(TAG, "❌ $error")
                    Result.failure(Exception(error))
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción al obtener valores", e)
                Result.failure(e)
            }
        }

    /**
     * Crea una nueva personalización en el servidor
     * POST /api/personalizaciones
     */
    suspend fun crearPersonalizacion(
        estado: PersonalizacionState,
        usuarioId: Int?
    ): Result<PersonalizacionGuardada> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Guardando personalizacion...")

            val valoresIds = estado.obtenerIdsSeleccionados()

            Log.d(TAG, "Usuario: $usuarioId")
            Log.d(TAG, "Valores: ${valoresIds.joinToString(", ")}")

            // Usar el método factory
            val request = PersonalizacionRequestDTO.crearConFechaActual(
                usuarioId = usuarioId,
                valores = valoresIds
            )

            val response = api.crearPersonalizacion(request)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.data != null) {
                    Log.d(TAG, "✅ Personalización guardada. ID: ${body.data.id}")

                    val personalizacion = PersonalizacionGuardada(
                        id = body.data.id,
                        estado = estado
                    )

                    Result.success(personalizacion)
                } else {
                    val error = body?.message ?: "Respuesta sin datos"
                    Log.e(TAG, "❌ $error")
                    Result.failure(Exception(error))
                }
            } else {
                val error = "Error ${response.code()}: ${response.message()}"
                Log.e(TAG, "❌ $error")
                Result.failure(Exception(error))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al guardar", e)
            Result.failure(e)
        }
    }
}

/**
 * Representa una personalización guardada exitosamente
 */
data class PersonalizacionGuardada(
    val id: Int,
    val estado: PersonalizacionState
)