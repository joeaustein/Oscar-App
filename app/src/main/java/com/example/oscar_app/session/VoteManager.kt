package com.example.oscar_app.session

import com.example.oscar_app.models.VotoLocal

object VoteManager {
    var voto = VotoLocal()

    fun registrarFilme(id: String, nome: String) {
        if (!voto.confirmado) {
            voto.filmeId = id
            voto.filmeNome = nome
        }
    }

    fun registrarDiretor(id: String, nome: String) {
        if (!voto.confirmado) {
            voto.diretorId = id
            voto.diretorNome = nome
        }
    }

    fun reset() {
        voto = VotoLocal()
    }
}