package com.example.oscar_app.models

data class VoteRequest(
    val usuarioId: Int,
    val filmeId: String,
    val diretorId: String,
    val token: Int
)