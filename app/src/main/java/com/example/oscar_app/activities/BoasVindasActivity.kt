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
        // Interface moderna ocupando toda a área da tela
        enableEdgeToEdge()
        binding = ActivityBoasVindasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplica o distanciamento correto das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // PASSO 1: Sincronização e Bloqueio - Verifica se o usuário já confirmou o voto
        if (sessionManager.hasVoted()) {
            // Sincroniza o estado global do app com os dados salvos
            VoteManager.voto.confirmado = true
            VoteManager.voto.filmeNome = sessionManager.getVotedFilme()
            VoteManager.voto.diretorNome = sessionManager.getVotedDiretor()
            
            // Mostra o que foi votado e bloqueia botões
            exibirVotoConfirmado()
        }

        // PASSO 2: Exibição do Token aleatório gerado pelo servidor no momento do Login
        val token = sessionManager.getToken()
        // Mostra N/A se o token não existir
        binding.tvToken.text = if (token != -1) token.toString() else "N/A"

        // Configura cliques e estados iniciais dos botões
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        // PASSO 3: Atualização em tempo real ao voltar da tela de confirmação
        if (sessionManager.hasVoted()) {
            // Garante que a UI reflita o bloqueio imediatamente ao voltar pra home
            VoteManager.voto.confirmado = true
            VoteManager.voto.filmeNome = sessionManager.getVotedFilme()
            VoteManager.voto.diretorNome = sessionManager.getVotedDiretor()
            
            exibirVotoConfirmado()
            setupButtons()
        }
    }

    // PASSO 4: Lógica de Feedback visual para voto já realizado
    private fun exibirVotoConfirmado() {
        // Torna visível a seção de resumo do voto
        binding.llVotoConfirmado.visibility = View.VISIBLE
        
        val filmeNome = sessionManager.getVotedFilme()
        val diretorNome = sessionManager.getVotedDiretor()

        // Se já temos os nomes salvos, exibe direto
        if (filmeNome != null && diretorNome != null) {
            binding.tvVotoResumo.text = "Filme: $filmeNome\nDiretor: $diretorNome"
        } else {
            // Se os nomes não estão em cache, busca na API para garantir exibição correta
            buscarNomesVotos()
        }
    }

    // PASSO 5: Busca assíncrona de detalhes do voto via GET
    private fun buscarNomesVotos() {
        val filmeId = sessionManager.getVotedFilmeId()
        val diretorId = sessionManager.getVotedDiretorId()

        // Se não houver IDs, não há o que buscar
        if (filmeId == null || diretorId == null) return

        // Busca a lista de filmes para encontrar o nome pelo ID
        apiService.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                val filme = response.body()?.find { it.id == filmeId }
                val fNome = filme?.nome ?: "Desconhecido"
                
                // Busca a lista de diretores para encontrar o nome pelo ID
                apiService.getDiretores().enqueue(object : Callback<List<Diretor>> {
                    override fun onResponse(call: Call<List<Diretor>>, response: Response<List<Diretor>>) {
                        val diretor = response.body()?.find { it.id == diretorId }
                        val dNome = diretor?.nome ?: "Desconhecido"
                        
                        // Atualiza a tela com os nomes encontrados
                        binding.tvVotoResumo.text = "Filme: $fNome\nDiretor: $dNome"
                        // Salva para não precisar buscar novamente nesta sessão
                        sessionManager.setHasVoted(true, fNome, dNome)
                    }
                    override fun onFailure(call: Call<List<Diretor>>, t: Throwable) {}
                })
            }
            override fun onFailure(call: Call<List<Filme>>, t: Throwable) {}
        })
    }

    // PASSO 6: Menu de Navegação - Bloqueia ações se o voto estiver confirmado
    private fun setupButtons() {
        if (sessionManager.hasVoted()) {
            // Muda o texto do botão de confirmação
            binding.btnConfirmarVoto.setText(com.example.oscar_app.R.string.btn_ver_votos)
            
            // Desabilita as opções de alteração (Requisito de Bloqueio)
            binding.btnVotarFilme.isEnabled = false
            binding.btnVotarFilme.alpha = 0.5f // Feedback visual de desabilitado
            
            binding.btnVotarDiretor.isEnabled = false
            binding.btnVotarDiretor.alpha = 0.5f
            
            binding.btnConfirmarVoto.isEnabled = false
            binding.btnConfirmarVoto.alpha = 0.5f
        }

        // Listener para abrir lista de filmes
        binding.btnVotarFilme.setOnClickListener {
            startActivity(Intent(this, ListaFilmesActivity::class.java))
        }

        // Listener para abrir votação de diretor
        binding.btnVotarDiretor.setOnClickListener {
            startActivity(Intent(this, VotarDiretorActivity::class.java))
        }

        // Listener para abrir tela de confirmação
        binding.btnConfirmarVoto.setOnClickListener {
            startActivity(Intent(this, ConfirmarVotoActivity::class.java))
        }

        // Listener para Logout (Limpa sessão e volta ao Login)
        binding.btnSair.setOnClickListener {
            sessionManager.clearSession()
            VoteManager.reset()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
