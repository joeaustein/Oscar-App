package com.example.oscar_app.activities

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oscar_app.R
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ActivityVotarDiretorBinding
import com.example.oscar_app.models.Diretor
import com.example.oscar_app.session.SessionManager
import com.example.oscar_app.session.VoteManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VotarDiretorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVotarDiretorBinding
    private lateinit var sessionManager: SessionManager
    private val apiService by lazy { OscarApiService.create() }
    private var diretoresList: List<Diretor> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVotarDiretorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        if (VoteManager.voto.confirmado || sessionManager.hasVoted()) {
            binding.btnConfirmarDiretor.isEnabled = false
            binding.btnConfirmarDiretor.text = "Voto Confirmado"
        }

        carregarDiretores()

        ////quando usuario clica em confrimar, o app ve qual RadioButton esta marcado
        //se nenhum estivar marcado, mostra: selecione um diretor
        binding.btnConfirmarDiretor.setOnClickListener {
            val checkedId = binding.rgDiretores.checkedRadioButtonId
            if (checkedId == -1) {
                Toast.makeText(this, "Selecione um diretor", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val radioButton = findViewById<RadioButton>(checkedId)
            val diretor = diretoresList.find { it.nome == radioButton.text }

            // se tiver um selecionado, registra o diretor no VoteManager
            diretor?.let {
                VoteManager.registrarDiretor(it.id, it.nome)
                Toast.makeText(this, "Voto em ${it.nome} registrado!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    //carrega os diretores da API
    private fun carregarDiretores() {
        binding.pbDiretor.visibility = View.VISIBLE
        apiService.getDiretores().enqueue(object : Callback<List<Diretor>> {
            override fun onResponse(call: Call<List<Diretor>>, response: Response<List<Diretor>>) {
                binding.pbDiretor.visibility = View.GONE
                if (response.isSuccessful) {
                    diretoresList = response.body() ?: emptyList()
                    popularRadioGroup(diretoresList)
                } else {
                    Toast.makeText(this@VotarDiretorActivity, "Erro ao carregar diretores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Diretor>>, t: Throwable) {
                binding.pbDiretor.visibility = View.GONE
                Toast.makeText(this@VotarDiretorActivity, "Falha na conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //enquanto carrega mostra ´bDiretor, quando recebe a resposta, salva a lista e chama
    private fun popularRadioGroup(diretores: List<Diretor>) {
        binding.rgDiretores.removeAllViews()
        diretores.forEach { diretor ->
            val rb = RadioButton(this)
            rb.text = diretor.nome
            rb.setTextColor(getColor(R.color.texto_principal))
            rb.id = View.generateViewId()
            binding.rgDiretores.addView(rb)
            
            // Marcar se já foi selecionado anteriormente
            if (diretor.id == VoteManager.voto.diretorId) {
                rb.isChecked = true
            }
        }
    }
}