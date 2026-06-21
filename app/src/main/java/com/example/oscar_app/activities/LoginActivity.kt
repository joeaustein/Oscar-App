package com.example.oscar_app.activities
// ela cria o binding o que faz coectar o kotlin ao layout activity_login
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.app.AlertDialog
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ActivityLoginBinding
import com.example.oscar_app.models.LoginRequest
import com.example.oscar_app.models.LoginResponse
import com.example.oscar_app.session.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // Injeção de dependências: Binding para UI, Session para persistência e API para rede
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val apiService by lazy { OscarApiService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ativa o layout que ocupa toda a tela, incluindo atrás das barras de sistema
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ajuste de padding para evitar que o conteúdo fique sob a barra de status ou navegação
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        // PASSO 1: Verificação automática de login para evitar re-autenticação desnecessária
        if (sessionManager.isLoggedIn()) {
            openBoasVindas()
        }

        binding.btnEntrar.setOnClickListener {
            // Captura o que foi digitado
            val login = binding.etLogin.text.toString()
            val senha = binding.etSenha.text.toString()

            // PASSO 2: Validação de campos vazios (requisito obrigatório)
            if (login.isBlank() || senha.isBlank()) {
                // Impede o envio se faltar dados
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Inicia o processo de login
            realizarLogin(login, senha)
        }
    }

    // PASSO 3: Consumo do serviço REST de Autenticação via POST
    private fun realizarLogin(login: String, senha: String) {
        val request = LoginRequest(login, senha)
        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // Verifica se a resposta foi 200 OK
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    // Verifica se a API retornou sucesso no JSON
                    if (loginResponse != null && loginResponse.success) {
                        // PASSO 4: Salvamento seguro dos dados da sessão e status de voto
                        sessionManager.saveSession(
                            userId = loginResponse.id ?: 0,
                            login = login,
                            token = loginResponse.token,
                            jaVotou = loginResponse.jaVotou
                        )
                        // Se já votou, guardamos os IDs para exibir na tela principal
                        if (loginResponse.jaVotou) {
                            sessionManager.saveVotedIds(
                                loginResponse.voto?.filmeId,
                                loginResponse.voto?.diretorId
                            )
                        }
                        // Vai para a tela inicial
                        openBoasVindas()
                    } else {
                        exibirErro("Falha na autenticação")
                    }
                } else {
                    // Tratamento de códigos HTTP semânticos (401, 400)
                    val msg = when (response.code()) {
                        401 -> "Login ou senha incorretos"
                        400 -> "Dados de login inválidos"
                        else -> "Erro no servidor: ${response.code()}"
                    }
                    exibirErro(msg)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                exibirErro("Falha na conexão: ${t.message}")
            }
        })
    }

    private fun exibirErro(mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle("Erro")
            .setMessage(mensagem)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun openBoasVindas() {
        startActivity(Intent(this, BoasVindasActivity::class.java))
        finish()
    }
}