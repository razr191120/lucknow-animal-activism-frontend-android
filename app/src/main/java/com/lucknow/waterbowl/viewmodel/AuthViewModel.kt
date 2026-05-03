package com.lucknow.waterbowl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.auth.AuthManager
import com.lucknow.waterbowl.data.models.LoginRequest
import com.lucknow.waterbowl.data.models.SignupRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.login(LoginRequest(email, password))
                AuthManager.setAuth(response.accessToken, response.user)
                _authSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            }
            _isLoading.value = false
        }
    }

    fun signup(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = api.signup(SignupRequest(email, fullName, password))
                AuthManager.setAuth(response.accessToken, response.user)
                _authSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Signup failed"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
