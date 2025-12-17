package com.example.oasisapp.network

import com.example.oasisapp.ui.screens.Device

// Adatmodellek az API válaszokhoz

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserData? = null
)

data class UserData(
    val id: Int,
    val name: String,
    val email: String
)

data class LoginRequest(
    val action: String = "login",
    val email: String,
    val password: String
)

data class RegisterRequest(
    val action: String = "register",
    val name: String,
    val email: String,
    val password: String
)

data class AddDeviceRequest(
    val action: String = "add_device",
    val user_id: Int,
    val name: String,
    val plant_name: String
)

data class AddDeviceResponse(
    val success: Boolean,
    val message: String,
    val device: Device? = null
)

data class WaterPlantRequest(
    val action: String = "water_plant",
    val device_id: String,
    val amount: Int
)

data class WaterPlantResponse(
    val success: Boolean,
    val message: String
)

data class SensorLogEntry(
    val id: Int,
    val box_id: Int,
    val timestamp: String,
    val szarassag: Float?,
    val feny: Float?,
    val ho: Float?,
    val para: Float?,
    val legnyomas: Float?,
    val vizszint: Float?
)
