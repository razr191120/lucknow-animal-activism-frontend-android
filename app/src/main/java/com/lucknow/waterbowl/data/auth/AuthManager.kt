package com.lucknow.waterbowl.data.auth

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.lucknow.waterbowl.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_USER = "user"

    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    val isLoggedIn: Boolean get() = _token.value != null
    val isAdmin: Boolean get() = _currentUser.value?.isAdmin == true

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _token.value = prefs.getString(KEY_TOKEN, null)
        val userJson = prefs.getString(KEY_USER, null)
        if (userJson != null) {
            try {
                _currentUser.value = gson.fromJson(userJson, User::class.java)
            } catch (_: Exception) { }
        }
    }

    fun setAuth(token: String, user: User) {
        _token.value = token
        _currentUser.value = user
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER, gson.toJson(user))
            .apply()
    }

    fun logout() {
        _token.value = null
        _currentUser.value = null
        prefs.edit().clear().apply()
    }

    fun updateUser(user: User) {
        _currentUser.value = user
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply()
    }
}
