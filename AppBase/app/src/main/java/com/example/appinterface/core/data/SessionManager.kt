package com.example.appinterface.core.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * SessionManager - Gestiona la sesión del usuario y el token JWT
 *
 * Responsabilidades:
 * - Guardar/recuperar token JWT
 * - Guardar/recuperar datos del usuario (username, roles)
 * - Verificar si hay sesión activa
 * - Cerrar sesión (limpiar datos)
 */
class SessionManager(context: Context) {

    companion object {
        private const val TAG = "SessionManager"
        private const val PREF_NAME = "brisas_prefs"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLES = "roles"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Guarda la sesión completa después del login exitoso
     */
    fun saveSession(
        token: String,
        tokenType: String = "Bearer",
        userId: Int,
        username: String,
        roles: List<String>
    ) {
        Log.d(TAG, "💾 Guardando sesión - userId: $userId, username: $username")

        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            putString(KEY_TOKEN_TYPE, tokenType)
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putStringSet(KEY_ROLES, roles.toSet())
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }

        // Verificar que se guardó correctamente
        Log.d(TAG, "✅ Sesión guardada - Verificación: getUserId() = ${getUserId()}")
    }

    /**
     * Obtiene el token JWT (incluye el tipo: "Bearer token...")
     */
    fun getAuthToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val tokenType = prefs.getString(KEY_TOKEN_TYPE, "Bearer")
        return "$tokenType $token"
    }

    /**
     * Obtiene el username del usuario logueado
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Obtiene los roles del usuario
     */
    fun getRoles(): Set<String> {
        return prefs.getStringSet(KEY_ROLES, emptySet()) ?: emptySet()
    }

    /**
     * Verifica si el usuario está logueado
     */
    fun isLoggedIn(): Boolean {
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false) &&
                !prefs.getString(KEY_TOKEN, null).isNullOrBlank()

        Log.d(TAG, "🔍 isLoggedIn: $loggedIn")
        return loggedIn
    }

    /**
     * Verifica si el usuario tiene rol de ADMIN
     */
    fun isAdmin(): Boolean {
        return getRoles().contains("ROLE_ADMINISTRADOR")
    }

    /**
     * CORREGIDO: Obtiene el ID del usuario logueado
     * Devuelve null si:
     * - No existe el valor
     * - Es 0 (inválido)
     * - Es -1 (valor por defecto)
     */
    fun getUserId(): Int? {
        val id = prefs.getInt(KEY_USER_ID, -1)

        Log.d(TAG, "🔍 getUserId() llamado - Valor guardado: $id")

        return when {
            id <= 0 -> {
                Log.w(TAG, "⚠️ getUserId() devuelve null - ID inválido: $id")
                null
            }
            else -> {
                Log.d(TAG, "✅ getUserId() devuelve: $id")
                id
            }
        }
    }

    /**
     * Cierra la sesión del usuario (limpia todos los datos)
     */
    fun logout() {
        Log.d(TAG, "🚪 Cerrando sesión...")

        prefs.edit().apply {
            remove(KEY_TOKEN)
            remove(KEY_TOKEN_TYPE)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_ROLES)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }

        Log.d(TAG, "✅ Sesión cerrada")
    }
}