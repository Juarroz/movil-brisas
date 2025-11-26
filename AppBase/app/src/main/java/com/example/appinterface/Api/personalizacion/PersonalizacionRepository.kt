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
     *
     * ⚠️ ACTUALIZADO: El backend responde directamente con el objeto,
     * no con wrapper success/data
     */
    suspend fun crearPersonalizacion(
        estado: PersonalizacionState,
        usuarioId: Int?
    ): Result<PersonalizacionGuardada> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 Guardando personalización...")

            val valoresIds = estado.obtenerIdsSeleccionados()

            // LOGS MEJORADOS
            Log.d(TAG, "═══════════════════════════════")
            Log.d(TAG, "📋 DATOS A ENVIAR:")
            Log.d(TAG, "   Usuario ID: $usuarioId")
            Log.d(TAG, "   Valores IDs: ${valoresIds.joinToString(", ")}")
            Log.d(TAG, "═══════════════════════════════")

            val request = PersonalizacionRequestDTO.crearConFechaActual(
                usuarioId = usuarioId,
                valores = valoresIds
            )

            // LOG DEL REQUEST COMPLETO
            Log.d(TAG, "📤 Request DTO:")
            Log.d(TAG, "   usuarioClienteId: ${request.usuarioClienteId}")
            Log.d(TAG, "   fecha: ${request.fecha}")
            Log.d(TAG, "   valoresSeleccionados: ${request.valoresSeleccionados}")

            val response = api.crearPersonalizacion(request)

            // LOG DE RESPUESTA
            Log.d(TAG, "📥 Response:")
            Log.d(TAG, "   isSuccessful: ${response.isSuccessful}")
            Log.d(TAG, "   code: ${response.code()}")
            Log.d(TAG, "   message: ${response.message()}")

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Log.d(TAG, "✅ Personalización guardada exitosamente")
                    Log.d(TAG, "   ID: ${body.id}")
                    Log.d(TAG, "   Fecha: ${body.fecha}")
                    Log.d(TAG, "   Usuario: ${body.usuarioClienteId}")
                    Log.d(TAG, "   Detalles: ${body.detalles.size} items")

                    val personalizacion = PersonalizacionGuardada(
                        id = body.id,
                        estado = estado,
                        detalles = body.detalles
                    )

                    Result.success(personalizacion)
                } else {
                    val error = "Respuesta vacía del servidor"
                    Log.e(TAG, "❌ $error")
                    Result.failure(Exception(error))
                }
            } else {
                // LOG DETALLADO DEL ERROR
                val errorBody = response.errorBody()?.string()
                val error = "Error ${response.code()}: ${response.message()}"

                Log.e(TAG, "❌ Error del servidor:")
                Log.e(TAG, "   Código: ${response.code()}")
                Log.e(TAG, "   Mensaje: ${response.message()}")
                Log.e(TAG, "   ErrorBody: $errorBody")

                Result.failure(Exception("$error - $errorBody"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Excepción al guardar", e)
            Log.e(TAG, "   Tipo: ${e.javaClass.simpleName}")
            Log.e(TAG, "   Mensaje: ${e.message}")
            Log.e(TAG, "   StackTrace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
}

/**
 * Representa una personalización guardada exitosamente
 */
data class PersonalizacionGuardada(
    val id: Int,
    val estado: PersonalizacionState,
    val detalles: List<DetallePersonalizacionCreado> = emptyList()
) {
    /**
     * Genera un resumen legible de la personalización
     */
    fun generarResumen(): String {
        val builder = StringBuilder()
        builder.appendLine("📋 RESUMEN DE PERSONALIZACIÓN")
        builder.appendLine("ID: $id")
        builder.appendLine()

        detalles.forEach { detalle ->
            builder.appendLine("• ${detalle.opcionNombre}: ${detalle.valorNombre}")
        }

        return builder.toString()
    }

    /**
     * Genera un resumen para incluir en el mensaje del formulario
     */
    fun generarResumenParaFormulario(): String {
        val builder = StringBuilder()
        builder.appendLine("Personalización de anillo (ID: $id)")
        builder.appendLine()

        detalles.forEach { detalle ->
            builder.appendLine("${detalle.opcionNombre}: ${detalle.valorNombre}")
        }

        return builder.toString()
    }
}