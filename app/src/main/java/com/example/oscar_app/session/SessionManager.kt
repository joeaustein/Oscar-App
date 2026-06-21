package com.example.oscar_app.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("oscar_session", Context.MODE_PRIVATE)

    fun saveSession(userId: Int, login: String, token: Int, jaVotou: Boolean = false) {
        prefs.edit().apply {
            putInt("userId", userId)
            putString("login", login)
            putInt("token", token)
            putBoolean("isLoggedIn", true)
            putBoolean("hasVoted", jaVotou)
            apply()
        }
    }

    fun saveVotedIds(filmeId: String?, diretorId: String?) {
        prefs.edit().apply {
            putString("filmeId", filmeId)
            putString("diretorId", diretorId)
            apply()
        }
    }

    fun getVotedFilmeId(): String? = prefs.getString("filmeId", null)
    fun getVotedDiretorId(): String? = prefs.getString("diretorId", null)

    fun setHasVoted(hasVoted: Boolean, filmeNome: String? = null, diretorNome: String? = null) {
        prefs.edit().apply {
            putBoolean("hasVoted", hasVoted)
            putString("filmeNome", filmeNome)
            putString("diretorNome", diretorNome)
            apply()
        }
    }

    fun getVotedFilme(): String? = prefs.getString("filmeNome", null)
    fun getVotedDiretor(): String? = prefs.getString("diretorNome", null)

    fun hasVoted(): Boolean = prefs.getBoolean("hasVoted", false)

    fun getUserId(): Int = prefs.getInt("userId", -1)
    fun getLogin(): String? = prefs.getString("login", null)
    fun getToken(): Int = prefs.getInt("token", -1)
    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}