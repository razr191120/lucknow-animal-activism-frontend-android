package com.lucknow.waterbowl.data.api

import com.lucknow.waterbowl.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("api/v1/auth/signup")
    suspend fun signup(@Body request: SignupRequest): TokenResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): TokenResponse

    @GET("api/v1/auth/me")
    suspend fun getMe(): User

    // Drives
    @POST("api/v1/drives/")
    suspend fun createDrive(@Body request: CreateDriveRequest): Drive

    @GET("api/v1/drives/")
    suspend fun getDrives(): List<Drive>

    @GET("api/v1/drives/{id}")
    suspend fun getDriveDetail(@Path("id") id: Int): Drive

    // Geocoding
    @POST("api/v1/geocode")
    suspend fun geocodeAddresses(@Body request: GeocodeRequest): List<GeocodeResult>

    @POST("api/v1/optimize-route")
    suspend fun optimizeRoute(@Body request: OptimizeRouteRequest): OptimizeRouteResponse

    // Distributions
    @Multipart
    @POST("api/v1/distributions/")
    suspend fun recordDistribution(
        @Part("name") name: RequestBody,
        @Part("contact") contact: RequestBody,
        @Part("description") description: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("address") address: RequestBody,
        @Part("drive_id") driveId: RequestBody,
        @Part waterBowlPhoto: MultipartBody.Part?,
        @Part ownerPhoto: MultipartBody.Part?
    ): Distribution

    @GET("api/v1/distributions/")
    suspend fun getDistributions(): List<Distribution>

    @GET("api/v1/stats")
    suspend fun getStats(): Stats

    // Admin endpoints
    @GET("api/v1/admin/users")
    suspend fun adminGetUsers(): List<User>

    @POST("api/v1/admin/users")
    suspend fun adminCreateUser(@Body request: SignupRequest): User

    @PATCH("api/v1/admin/users/{userId}")
    suspend fun adminUpdateUser(
        @Path("userId") userId: String,
        @Body updates: UserUpdateRequest
    ): User

    @PATCH("api/v1/admin/users/{userId}/password")
    suspend fun adminResetPassword(
        @Path("userId") userId: String,
        @Body request: PasswordResetRequest
    )

    @DELETE("api/v1/admin/users/{userId}")
    suspend fun adminDeleteUser(@Path("userId") userId: String)

    @DELETE("api/v1/admin/drives/{driveId}")
    suspend fun adminDeleteDrive(@Path("driveId") driveId: String)

    @DELETE("api/v1/admin/distributions/{distId}")
    suspend fun adminDeleteDistribution(@Path("distId") distId: String)
}
