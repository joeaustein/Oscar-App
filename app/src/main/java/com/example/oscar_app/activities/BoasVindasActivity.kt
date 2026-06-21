package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ActivityBoasVindasBinding
import com.example.oscar_app.models.Diretor
import com.example.oscar_app.models.Filme
import com.example.oscar_app.session.SessionManager
import com.example.oscar_app.session.VoteManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoasVindasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoasVindasBinding
    private lateinit var sessionManager: SessionManager
    private val apiService by lazy { OscarApiService.create() }

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
            
            exibirVotoConfirmado()
        }

        // Exibir token recebido
        val token = sessionManager.getToken()
        binding.tvToken.text = if (token != -1) token.toString() else "N/A"

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        // Recarregar status de voto ao voltar para esta tela
        if (sessionManager.hasVoted()) {
            VoteManager.voto.confirmado = true
            VoteManager.voto.filmeNome = sessionManager.getVotedFilme()
            VoteManager.voto.diretorNome = sessionManager.getVotedDiretor()
            
            exibirVotoConfirmado()
            setupButtons()
        }
    }

    private fun exibirVotoConfirmado() {
        binding.llVotoConfirmado.visibility = View.VISIBLE
        
        val filmeNome = sessionManager.getVotedFilme()
        val diretorNome = sessionManager.getVotedDiretor()

        if (filmeNome != null && diretorNome != null) {
            binding.tvVotoResumo.text = "Filme: $filmeNome\nDiretor: $diretorNome"
        } else {
            // Se não temos os nomes, buscamos pelos IDs
            buscarNomesVotos()
        }
    }

    private fun buscarNomesVotos() {
        val filmeId = sessionManager.getVotedFilmeId()
        val diretorId = sessionManager.getVotedDiretorId()

        if (filmeId == null || diretorId == null) return

        apiService.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                val filme = response.body()?.find { it.id == filmeId }
                val fNome = filme?.nome ?: "Desconhecido"
                
                apiService.getDiretores().enqueue(object : Callback<List<Diretor>> {
                    override fun onResponse(call: Call<List<Diretor>>, response: Response<List<Diretor>>) {
                        val diretor = response.body()?.find { it.id == diretorId }
                        val dNome = diretor?.nome ?: "Desconhecido"
                        
                        binding.tvVotoResumo.text = "Filme: $fNome\nDiretor: $dNome"
                        // Opcional: salvar nomes na sessão para evitar nova busca
                        sessionManager.setHasVoted(true, fNome, dNome)
                    }
                    override fun onFailure(call: Call<List<Diretor>>, t: Throwable) {}
                })
            }
            override fun onFailure(call: Call<List<Filme>>, t: Throwable) {}
        })
    }

    private fun setupButtons() {
        if (sessionManager.hasVoted()) {
            binding.btnConfirmarVoto.setText(com.example.oscar_app.R.string.btn_ver_votos)
            
            // Desabilitar e deixar acizentado (menos opacidade)
            binding.btnVotarFilme.isEnabled = false
            binding.btnVotarFilme.alpha = 0.5f
            
            binding.btnVotarDiretor.isEnabled = false
            binding.btnVotarDiretor.alpha = 0.5f
            
            binding.btnConfirmarVoto.isEnabled = false
            binding.btnConfirmarVoto.alpha = 0.5f
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