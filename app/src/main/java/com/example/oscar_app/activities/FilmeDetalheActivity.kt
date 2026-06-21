package com.example.oscar_app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.oscar_app.api.NetworkConfig
import com.example.oscar_app.databinding.ActivityFilmeDetalheBinding
import com.example.oscar_app.session.SessionManager
import com.example.oscar_app.session.VoteManager

class FilmeDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilmeDetalheBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFilmeDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // PASSO 1: Recuperação dos dados passados pela Lista
        val id = intent.getStringExtra("FILME_ID") ?: ""
        val nome = intent.getStringExtra("FILME_NOME") ?: ""
        val genero = intent.getStringExtra("FILME_GENERO") ?: ""
        val foto = intent.getStringExtra("FILME_FOTO") ?: ""

        // Atualiza os textos da interface
        binding.tvNomeDetalhe.text = nome
        binding.tvGeneroDetalhe.text = genero
        
        // PASSO 2: Carregamento Assíncrono da Imagem (Requisito: Glide + URL do JSON)
        val urlCompleta = if (foto.startsWith("http")) {
            // Se a URL já for completa, usa ela
            foto
        } else {
            // Se for relativa, concatena com o endereço do servidor
            NetworkConfig.BASE_URL + foto
        }
        // O Glide gerencia o download e o cache da imagem automaticamente
        Glide.with(this).load(urlCompleta).into(binding.ivPosterDetalhe)

        // PASSO 3: Bloqueio de Edição - Verifica se o voto geral já foi confirmado
        if (VoteManager.voto.confirmado || sessionManager.hasVoted()) {
            // Impede nova escolha se o processo de votação já encerrou
            binding.btnVotarFilmeDetalhe.isEnabled = false
            binding.btnVotarFilmeDetalhe.text = "Voto Confirmado"
        }

        binding.btnVotarFilmeDetalhe.setOnClickListener {
            // PASSO 4: Registro de Voto Local (Ainda não enviado ao servidor)
            VoteManager.registrarFilme(id, nome)
            // Feedback de sucesso para o usuário
            Toast.makeText(this, "Voto em $nome registrado localmente!", Toast.LENGTH_SHORT).show()
            // Fecha a tela para voltar ao menu
            finish()
        }
    }
}