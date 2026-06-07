package com.example.oscar_app.models

data class LoginResponse(
    val success: Boolean,
    val token: Int,
    val id: Int? = null // Added to store userId as per Phase 8
)