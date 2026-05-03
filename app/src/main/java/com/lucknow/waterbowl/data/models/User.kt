package com.lucknow.waterbowl.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val role: String,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) {
    val isAdmin: Boolean get() = role == "admin"
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val email: String,
    @SerializedName("full_name") val fullName: String,
    val password: String
)

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: User
)

data class UserUpdateRequest(
    @SerializedName("full_name") val fullName: String? = null,
    val email: String? = null,
    val role: String? = null,
    @SerializedName("is_active") val isActive: Boolean? = null
)

data class PasswordResetRequest(
    @SerializedName("new_password") val newPassword: String
)
