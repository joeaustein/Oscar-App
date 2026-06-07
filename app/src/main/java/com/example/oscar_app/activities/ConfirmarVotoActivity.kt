package com.example.oscar_app.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityConfirmarVotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.tvFilmeEscolhido.text = VoteManager.voto.filmeNome ?: "Não selecionado"
        binding.tvDiretorEscolhido.text = VoteManager.voto.diretorNome ?: "Não selecionado"

        if (sessionManager.hasVoted()) {
            binding.btnFinalizarVoto.isEnabled = false
            binding.etTokenConfirm.isEnabled = false
            binding.btnFinalizarVoto.text = "Voto já realizado"
        }

        binding.btnFinalizarVoto.setOnClickListener {
            val tokenInput = binding.etTokenConfirm.text.toString()
            
            if (tokenInput.isBlank()) {
                Toast.makeText(this, "Informe o token", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val filmeId = VoteManager.voto.filmeId
            val diretorId = VoteManager.voto.diretorId

            if (filmeId == null || diretorId == null) {
                Toast.makeText(this, "Selecione um filme e um diretor primeiro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            finalizarVoto(filmeId, diretorId, tokenInput.toInt())
        }
    }

    private fun finalizarVoto(filmeId: String, diretorId: String, token: Int) {
        binding.pbConfirmar.visibility = View.VISIBLE
        
        val request = VoteRequest(
            usuarioId = sessionManager.getUserId(),
            filmeId = filmeId,
            diretorId = diretorId,
            token = token
        )

        apiService.confirmarVoto(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                binding.pbConfirmar.visibility = View.GONE
                if (response.isSuccessful) {
                    sessionManager.setHasVoted(true, VoteManager.voto.filmeNome, VoteManager.voto.diretorNome)
                    VoteManager.voto.confirmado = true
                    exibirFeedback(true, "Voto registrado com sucesso!")
                } else {
                    val msg = when (response.code()) {
                        401 -> "Token inválido"
                        400 -> "Usuário já votou ou dados inválidos"
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

    private fun exibirFeedback(sucesso: Boolean, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(if (sucesso) "Sucesso" else "Erro")
            .setMessage(mensagem)
            .setPositiveButton("OK") { _, _ ->
                if (sucesso) finish()
            }
            .setCancelable(false)
            .show()
    }
}