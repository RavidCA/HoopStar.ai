package com.starhoop.hoopstar.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun saveCoach(coachId: Int, displayName: String?, email: String?) {
        prefs.edit()
            .putInt(KEY_COACH_ID, coachId)
            .putString(KEY_DISPLAY_NAME, displayName)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun getCoachId(): Int = prefs.getInt(KEY_COACH_ID, -1)
    fun getDisplayName(): String? = prefs.getString(KEY_DISPLAY_NAME, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun clear() = prefs.edit().clear().apply()

    companion object {
        private const val PREFS_NAME = "hoopstar_secure_prefs"
        private const val KEY_TOKEN = "access_token"
        private const val KEY_COACH_ID = "coach_id"
        private const val KEY_DISPLAY_NAME = "display_name"
        private const val KEY_EMAIL = "email"
    }
}