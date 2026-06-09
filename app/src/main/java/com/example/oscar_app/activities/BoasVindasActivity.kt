package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oscar_app.databinding.ActivityBoasVindasBinding
import com.example.oscar_app.session.SessionManager
import com.example.oscar_app.session.VoteManager

class BoasVindasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoasVindasBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBoasVindasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // Sincronizar VoteManager com a sessão (caso o usuário já tenha votado)
        if (sessionManager.hasVoted()) {
            VoteManager.voto.confirmado = true
            VoteManager.voto.filmeNome = sessionManager.getVotedFilme()
            VoteManager.voto.diretorNome = sessionManager.getVotedDiretor()
        }

        // Exibir token recebido
        val token = sessionManager.getToken()
        binding.tvToken.text = if (token != -1) token.toString() else "N/A"

        setupButtons()
    }

    private fun setupButtons() {
        if (sessionManager.hasVoted()) {
            binding.btnConfirmarVoto.setText(com.example.oscar_app.R.string.btn_ver_votos)
        }

        binding.btnVotarFilme.setOnClickListener {
            startActivity(Intent(this, ListaFilmesActivity::class.java))
        }

        binding.btnVotarDiretor.setOnClickListener {
            startActivity(Intent(this, VotarDiretorActivity::class.java))
        }

        binding.btnConfirmarVoto.setOnClickListener {
            startActivity(Intent(this, ConfirmarVotoActivity::class.java))
        }

        binding.btnSair.setOnClickListener {
            sessionManager.clearSession()
            VoteManager.reset()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}