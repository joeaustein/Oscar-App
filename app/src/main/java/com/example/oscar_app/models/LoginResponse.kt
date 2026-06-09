package com.example.oscar_app.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val token: Int,
    @SerializedName("usuarioId")
    val id: Int? = null
)