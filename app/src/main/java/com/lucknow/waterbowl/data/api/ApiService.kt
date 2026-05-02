package com.lucknow.waterbowl.data.api

import com.lucknow.waterbowl.data.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/drives/")
    suspend fun createDrive(@Body request: CreateDriveRequest): Drive

    @GET("api/v1/drives/")
    suspend fun getDrives(): List<Drive>

    @GET("api/v1/drives/{id}")
    suspend fun getDriveDetail(@Path("id") id: Int): Drive

    @POST("api/v1/geocode")
    suspend fun geocodeAddresses(@Body request: GeocodeRequest): List<GeocodeResult>

    @POST("api/v1/optimize-route")
    suspend fun optimizeRoute(@Body request: OptimizeRouteRequest): OptimizeRouteResponse

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
}
