package com.example.appinterface.Api.personalizacion

import android.util.Log
import com.example.appinterface.core.ApiServicesKotlin
import com.example.appinterface.core.RetrofitInstance

import retrofit2.Response

/**
 * Repository para gestionar todas las operaciones de personalización
 * Actúa como intermediario entre la UI y la API
 */
class PersonalizacionRepository {

    private val apiService: ApiServicesKotlin = RetrofitInstance.api2kotlin

    companion object {
        private const val TAG = "PersonalizacionRepo"
    }

    // ==========================================
    // OPCIONES (Categorías)
    // ==========================================

    /**
     * Obtiene todas las categorías de personalización
     * @return Result con lista de opciones o error
     */
    suspend fun obtenerOpciones(): Result<List<PersonalizacionOpcion>> {
        return try {
            val response = apiService.obtenerOpciones()

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Opciones obtenidas: ${body.data.size}")
                    Result.success(body.data)
                } else {
                    val error = body?.message ?: "Error al obtener opciones"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "Error HTTP: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener opciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene una opción específica por ID
     */
    suspend fun obtenerOpcionPorId(opcionId: Int): Result<PersonalizacionOpcion> {
        return try {
            val response = apiService.obtenerOpcionPorId(opcionId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data.isNotEmpty()) {
                    Result.success(body.data.first())
                } else {
                    Result.failure(Exception(body?.message ?: "Opción no encontrada"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener opción $opcionId", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // VALORES (Opciones específicas)
    // ==========================================

    /**
     * Obtiene todos los valores de personalización
     * @param opcionId Filtro opcional por categoría
     * @param busqueda Búsqueda opcional por nombre
     * @return Result con lista de valores o error
     */
    suspend fun obtenerValores(
        opcionId: Int? = null,
        busqueda: String? = null
    ): Result<List<PersonalizacionValor>> {
        return try {
            val response = apiService.obtenerValores(opcionId, busqueda)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Valores obtenidos: ${body.data.size}")
                    Result.success(body.data)
                } else {
                    val error = body?.message ?: "Error al obtener valores"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "Error HTTP: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al obtener valores", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene valores filtrados por categoría y solo combinaciones disponibles
     * @param opcionId ID de la categoría
     * @return Lista de valores válidos para mostrar en UI
     */
    suspend fun obtenerValoresDisponibles(opcionId: Int): Result<List<PersonalizacionValor>> {
        return try {
            val resultado = obtenerValores(opcionId = opcionId)

            resultado.map { valores ->
                // Determinar la categoría
                val categoria = if (valores.isNotEmpty()) {
                    val opcionNombre = valores.first().opcionNombre ?: ""
                    when {
                        opcionNombre.contains("forma", ignoreCase = true) -> "forma"
                        opcionNombre.contains("gema", ignoreCase = true) -> "gema"
                        opcionNombre.contains("material", ignoreCase = true) -> "material"
                        else -> "otros"
                    }
                } else {
                    "otros"
                }

                // Filtrar solo valores con combinaciones disponibles
                CombinacionesDisponibles.filtrarValoresDisponibles(valores, categoria)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener valores disponibles", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene un valor específico por ID
     */
    suspend fun obtenerValorPorId(valorId: Int): Result<PersonalizacionValor> {
        return try {
            val response = apiService.obtenerValorPorId(valorId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data.isNotEmpty()) {
                    Result.success(body.data.first())
                } else {
                    Result.failure(Exception(body?.message ?: "Valor no encontrado"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener valor $valorId", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // PERSONALIZACIONES (Transacciones)
    // ==========================================

    /**
     * Crea una nueva personalización completa
     * @param estado Estado actual de la personalización
     * @param usuarioId ID del usuario (null si no está logueado)
     * @return Result con la personalización creada o error
     */
    suspend fun crearPersonalizacion(
        estado: PersonalizacionState,
        usuarioId: Int?
    ): Result<PersonalizacionCreada> {
        return try {
            // Validar que el estado sea válido
            if (!estado.esValido()) {
                val error = estado.obtenerMensajeError() ?: "Personalización incompleta"
                return Result.failure(Exception(error))
            }

            // Validar que la combinación tenga imágenes disponibles
            if (!CombinacionesDisponibles.esCombinaciónValida(
                    estado.formaSlug,
                    estado.gemaSlug,
                    estado.materialSlug
                )) {
                val sugerencias = CombinacionesDisponibles.obtenerSugerencias(
                    estado.formaSlug,
                    estado.gemaSlug,
                    estado.materialSlug
                )
                return Result.failure(Exception("Combinación no disponible.\n$sugerencias"))
            }

            // Crear el request
            val request = PersonalizacionRequestDTO.desdeEstado(estado, usuarioId)
            Log.d(TAG, "Creando personalización con ${request.valores.size} valores")

            val response = apiService.crearPersonalizacion(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    Log.d(TAG, "Personalización creada con ID: ${body.data.id}")
                    Result.success(body.data)
                } else {
                    val error = body?.message ?: "Error al crear personalización"
                    Log.e(TAG, error)
                    Result.failure(Exception(error))
                }
            } else {
                val error = "Error HTTP: ${response.code()} - ${response.message()}"
                Log.e(TAG, error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Excepción al crear personalización", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las personalizaciones con filtros opcionales
     */
    suspend fun obtenerPersonalizaciones(
        clienteId: Int? = null,
        fechaDesde: String? = null,
        fechaHasta: String? = null
    ): Result<List<PersonalizacionResumen>> {
        return try {
            val response = apiService.obtenerPersonalizaciones(clienteId, fechaDesde, fechaHasta)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Personalizaciones obtenidas: ${body.data.size}")
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener personalizaciones"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener personalizaciones", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene una personalización específica
     */
    suspend fun obtenerPersonalizacionPorId(personalizacionId: Int): Result<PersonalizacionData> {
        return try {
            val response = apiService.obtenerPersonalizacionPorId(personalizacionId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Personalización no encontrada"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener personalización $personalizacionId", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina una personalización
     */
    suspend fun eliminarPersonalizacion(personalizacionId: Int): Result<Boolean> {
        return try {
            val response = apiService.eliminarPersonalizacion(personalizacionId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Personalización $personalizacionId eliminada")
                    Result.success(true)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al eliminar"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar personalización", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // DETALLES
    // ==========================================

    /**
     * Obtiene los detalles completos de una personalización
     * @return Result con lista de detalles que incluyen nombres de valores y opciones
     */
    suspend fun obtenerDetallesPersonalizacion(
        personalizacionId: Int
    ): Result<List<DetallePersonalizacion>> {
        return try {
            val response = apiService.obtenerDetallesPersonalizacion(personalizacionId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Detalles obtenidos: ${body.data.size}")
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al obtener detalles"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener detalles", e)
            Result.failure(e)
        }
    }

    /**
     * Agrega un detalle individual a una personalización
     * Útil si se permite editar personalizaciones existentes
     */
    suspend fun agregarDetalle(
        personalizacionId: Int,
        valorId: Int
    ): Result<DetalleCreado> {
        return try {
            val request = PersonalizacionDetalleRequestDTO(valorId)
            val response = apiService.agregarDetallePersonalizacion(personalizacionId, request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true && body.data != null) {
                    Log.d(TAG, "Detalle agregado: ${body.data.detalleId}")
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al agregar detalle"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al agregar detalle", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina un detalle específico
     */
    suspend fun eliminarDetalle(
        personalizacionId: Int,
        detalleId: Int
    ): Result<Boolean> {
        return try {
            val response = apiService.eliminarDetalle(personalizacionId, detalleId)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    Log.d(TAG, "Detalle $detalleId eliminado")
                    Result.success(true)
                } else {
                    Result.failure(Exception(body?.message ?: "Error al eliminar detalle"))
                }
            } else {
                Result.failure(Exception("Error HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar detalle", e)
            Result.failure(e)
        }
    }

    // ==========================================
    // MÉTODOS HELPER
    // ==========================================

    /**
     * Reconstruye un PersonalizacionState desde los detalles guardados
     * Útil para cargar una personalización existente
     */
    suspend fun cargarEstadoDesdePersonalizacion(
        personalizacionId: Int
    ): Result<PersonalizacionState> {
        return try {
            val detallesResult = obtenerDetallesPersonalizacion(personalizacionId)

            detallesResult.map { detalles ->
                val estado = PersonalizacionState()

                // Mapear cada detalle al estado
                detalles.forEach { detalle ->
                    val categoria = detalle.obtenerCategoria()

                    // Obtener el valor completo para tener el slug
                    val valorResult = obtenerValorPorId(detalle.valorId)
                    valorResult.getOrNull()?.let { valor ->
                        estado.actualizar(categoria, valor)
                    }
                }

                estado
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar estado desde personalización", e)
            Result.failure(e)
        }
    }

    /**
     * Valida si una personalización es válida antes de guardar
     */
    fun validarEstado(estado: PersonalizacionState): Result<Boolean> {
        return if (!estado.esValido()) {
            Result.failure(Exception(estado.obtenerMensajeError()))
        } else if (!CombinacionesDisponibles.esCombinaciónValida(
                estado.formaSlug,
                estado.gemaSlug,
                estado.materialSlug
            )) {
            val sugerencias = CombinacionesDisponibles.obtenerSugerencias(
                estado.formaSlug,
                estado.gemaSlug,
                estado.materialSlug
            )
            Result.failure(Exception("Combinación no disponible.\n$sugerencias"))
        } else {
            Result.success(true)
        }
    }
}