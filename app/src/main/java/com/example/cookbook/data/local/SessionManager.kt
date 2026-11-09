package com.example.cookbook.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

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
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_NAME = "cookbook_secure_session"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_TOKEN_TYPE = "token_type"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
    }

    fun saveSession(token: String, tokenType: String = "bearer", email: String? = null, name: String? = null) {
        sharedPreferences.edit().apply {
            putString(KEY_ACCESS_TOKEN, token)
            putString(KEY_TOKEN_TYPE, tokenType)
            if (email != null) putString(KEY_USER_EMAIL, email)
            if (name != null) putString(KEY_USER_NAME, name)
            apply()
        }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getTokenType(): String {
        return sharedPreferences.getString(KEY_TOKEN_TYPE, "bearer") ?: "bearer"
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun isLoggedIn(): Boolean {
        val token = getAuthToken()
        return !token.isNullOrBlank()
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
