package com.example.oscar_app.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val token: Int,
    @SerializedName("usuarioId")
    val id: Int? = null,
    val jaVotou: Boolean = false,
    val voto: VotoSimplificado? = null
)

data class VotoSimplificado(
    val filmeId: String?,
    val diretorId: String?
)