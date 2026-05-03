package com.lucknow.waterbowl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucknow.waterbowl.data.api.ApiExceptionMapper
import com.lucknow.waterbowl.data.api.RetrofitClient
import com.lucknow.waterbowl.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _drives = MutableStateFlow<List<Drive>>(emptyList())
    val drives: StateFlow<List<Drive>> = _drives.asStateFlow()

    private val _distributions = MutableStateFlow<List<Distribution>>(emptyList())
    val distributions: StateFlow<List<Distribution>> = _distributions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _users.value = api.adminGetUsers()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDrives() {
        viewModelScope.launch {
            try {
                _drives.value = api.getDrives()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun loadDistributions() {
        viewModelScope.launch {
            try {
                _distributions.value = api.getDistributions()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun createUser(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                api.adminCreateUser(SignupRequest(email, fullName, password))
                _message.value = "User created"
                loadUsers()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun toggleRole(user: User) {
        viewModelScope.launch {
            try {
                val newRole = if (user.isAdmin) "member" else "admin"
                api.adminUpdateUser(user.id, UserUpdateRequest(role = newRole))
                loadUsers()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun toggleActive(user: User) {
        viewModelScope.launch {
            try {
                api.adminUpdateUser(user.id, UserUpdateRequest(isActive = !user.isActive))
                loadUsers()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                api.adminDeleteUser(userId)
                _message.value = "User deleted"
                loadUsers()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun deleteDrive(driveId: String) {
        viewModelScope.launch {
            try {
                api.adminDeleteDrive(driveId)
                _message.value = "Drive deleted"
                loadDrives()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun deleteDistribution(distId: String) {
        viewModelScope.launch {
            try {
                api.adminDeleteDistribution(distId)
                _message.value = "Distribution deleted"
                loadDistributions()
            } catch (e: Exception) {
                _error.value = ApiExceptionMapper.userMessage(e)
            }
        }
    }

    fun clearMessage() { _message.value = null }
    fun clearError() { _error.value = null }
}
