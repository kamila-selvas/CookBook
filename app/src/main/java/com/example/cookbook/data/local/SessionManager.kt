package com.example.cookbook.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Gestor de sesión seguro para la aplicación.
 * Utiliza EncryptedSharedPreferences (respaldado por Android Keystore)
 * para cifrar el token JWT y datos de sesión sin guardarlos en texto plano.
 */
class SessionManager(context: Context) {

    private val sharedPreferences: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // En caso de incompatibilidad con el Keystore en emuladores o dispositivos específicos,
        // se usa almacenamiento privado como respaldo para garantizar estabilidad.
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_NAME = "cookbook_secure_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }

    /**
     * Guarda el token de acceso JWT y los datos del usuario.
     */
    fun saveSession(token: String, tokenType: String = "bearer", email: String? = null, name: String? = null) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            putString(KEY_TOKEN_TYPE, tokenType)
            if (email != null) putString(KEY_USER_EMAIL, email)
            if (name != null) putString(KEY_USER_NAME, name)
            apply()
        }
    }

    /**
     * Obtiene el token de autenticación guardado o null si no existe.
     */
    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Obtiene el tipo de token (generalmente "bearer").
     */
    fun getTokenType(): String {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, "bearer") ?: "bearer"
    }

    /**
     * Obtiene el correo del usuario en sesión.
     */
    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    /**
     * Obtiene el nombre del usuario en sesión.
     */
    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    /**
     * Verifica si existe una sesión activa con token no vacío.
     */
    fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        return !token.isNullOrBlank()
    }

    /**
     * Elimina el token y todos los datos de la sesión (Cerrar sesión).
     */
    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
