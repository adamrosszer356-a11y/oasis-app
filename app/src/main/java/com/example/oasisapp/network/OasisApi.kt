package com.example.oasisapp.network

import com.example.oasisapp.ui.screens.Device
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OasisApi {
    @GET("api.php")
    suspend fun getDevices(
        @Query("action") action: String = "get_devices",
        @Query("user_id") userId: String
    ): List<Device>

    @POST("api.php")
    suspend fun login(
        @Body request: LoginRequest,
        @Query("action") action: String = "login"
    ): AuthResponse

    @POST("api.php")
    suspend fun register(
        @Body request: RegisterRequest,
        @Query("action") action: String = "register"
    ): AuthResponse

    @POST("api.php")
    suspend fun addDevice(
        @Body request: AddDeviceRequest,
        @Query("action") action: String = "add_device"
    ): AddDeviceResponse

    @POST("api.php")
    suspend fun waterPlant(
        @Body request: WaterPlantRequest,
        @Query("action") action: String = "water_plant"
    ): WaterPlantResponse

    @GET("api.php")
    suspend fun getDeviceLog(
        @Query("action") action: String = "get_device_log",
        @Query("box_id") boxId: String,
        @Query("limit") limit: Int = 100
    ): List<SensorLogEntry>
}
