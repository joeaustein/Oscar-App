package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.oscar_app.databinding.ActivityBoasVindasBinding
import com.example.oscar_app.session.SessionManager

class BoasVindasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoasVindasBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoasVindasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        // Exibir token recebido
        val token = sessionManager.getToken()
        binding.tvToken.text = if (token != -1) token.toString() else "N/A"

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnVotarFilme.setOnClickListener {
            // Ir para Votar Filme (será implementado na fase 10)
        }

        binding.btnVotarDiretor.setOnClickListener {
            // Ir para Votar Diretor (será implementado na fase 12)
        }

        binding.btnConfirmarVoto.setOnClickListener {
            // Ir para Confirmar Voto (será implementado na fase 14)
        }

        binding.btnSair.setOnClickListener {
            sessionManager.clearSession()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}