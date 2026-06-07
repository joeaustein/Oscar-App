package com.example.oscar_app.models

data class VotoLocal(
    var filmeId: String? = null,
    var filmeNome: String? = null,
    var diretorId: String? = null,
    var diretorNome: String? = null,
    var confirmado: Boolean = false
)