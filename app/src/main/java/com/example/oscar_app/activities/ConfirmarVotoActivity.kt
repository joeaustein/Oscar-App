package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ActivityConfirmarVotoBinding
import com.example.oscar_app.models.VoteRequest
import com.example.oscar_app.session.SessionManager
import com.example.oscar_app.session.VoteManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConfirmarVotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmarVotoBinding
    private lateinit var sessionManager: SessionManager
    private val apiService by lazy { OscarApiService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityConfirmarVotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // PASSO 1: Revisão dos Votos - Exibe o que foi selecionado localmente no VoteManager
        binding.tvFilmeEscolhido.text = VoteManager.voto.filmeNome ?: "Não selecionado"
        binding.tvDiretorEscolhido.text = VoteManager.voto.diretorNome ?: "Não selecionado"

        // PASSO 2: Bloqueio de Segurança - Impede nova confirmação se já houver registro no BD
        if (sessionManager.hasVoted()) {
            // Desabilita as interações se o voto já estiver concluído
            binding.btnFinalizarVoto.isEnabled = false
            binding.etTokenConfirm.isEnabled = false
            binding.btnFinalizarVoto.text = "Voto já realizado"
        }

        binding.btnFinalizarVoto.setOnClickListener {
            // Captura o token digitado pelo usuário
            val tokenInput = binding.etTokenConfirm.text.toString()
            
            // Requisito: Bloqueio de campos vazios
            if (tokenInput.isBlank()) {
                Toast.makeText(this, "Informe o token", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filmeId = VoteManager.voto.filmeId
            val diretorId = VoteManager.voto.diretorId

            // Requisito: Validação de seleção completa antes do envio definitivo
            if (filmeId == null || diretorId == null) {
                Toast.makeText(this, "Selecione um filme e um diretor primeiro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inicia o envio para o servidor
            finalizarVoto(filmeId, diretorId, tokenInput.toInt())
        }
    }

    // PASSO 3: Consumo do serviço REST de Registro de Voto via POST
    private fun finalizarVoto(filmeId: String, diretorId: String, token: Int) {
        // Mostra o progresso do envio
        binding.pbConfirmar.visibility = View.VISIBLE
        
        // Cria o objeto de requisição com os IDs e o Token
        val request = VoteRequest(
            usuarioId = sessionManager.getUserId(),
            filmeId = filmeId,
            diretorId = diretorId,
            token = token
        )

        // Envia para o endpoint de registro de voto
        apiService.confirmarVoto(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                binding.pbConfirmar.visibility = View.GONE
                if (response.isSuccessful) {
                    // PASSO 4: Persistência do sucesso localmente no SessionManager
                    sessionManager.setHasVoted(true, VoteManager.voto.filmeNome, VoteManager.voto.diretorNome)
                    // Marca o voto como confirmado no estado global
                    VoteManager.voto.confirmado = true
                    exibirFeedback(true, "Voto registrado com sucesso!")
                } else {
                    // Tratamento de erros específicos: Token inválido (403), Já votou (409)
                    val msg = when (response.code()) {
                        403 -> "Token inválido"
                        409 -> "Usuário já possui um voto registrado"
                        404 -> "Usuário não encontrado"
                        else -> "Erro no servidor: ${response.code()}"
                    }
                    exibirFeedback(false, msg)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                binding.pbConfirmar.visibility = View.GONE
                exibirFeedback(false, "Falha na conexão: ${t.message}")
            }
        })
    }

    // PASSO 5: Feedback ao Usuário e Navegação de Recarregamento
    private fun exibirFeedback(sucesso: Boolean, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(if (sucesso) "Sucesso" else "Erro")
            .setMessage(mensagem)
            .setPositiveButton("OK") { _, _ ->
                if (sucesso) {
                    // Recarrega a tela de Boas-Vindas para refletir o novo status de bloqueio
                    val intent = Intent(this, BoasVindasActivity::class.java)
                    // Limpa a pilha de activities anteriores para garantir um estado limpo
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    // Encerra a tela atual
                    finish()
                }
            }
            .setCancelable(false) // Obriga o usuário a clicar no botão para fechar
            .show()
    }
}