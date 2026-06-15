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

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val apiService by lazy { OscarApiService.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sessionManager = SessionManager(this)

        //depois ele cria p session manager, onde cuida da sessao usuario, se o usuario ja tiver logado
        // ele pula para tela de boas vindas
        if (sessionManager.isLoggedIn()) {
            openBoasVindas()
        }

        //quando o usuario clica no botao de entrar, o codigo pega os campos da tela
        // se algum estiver vazio, mostra um TOAST. Se tiver tudo preenchido, chama BoasVindasActivity
        binding.btnEntrar.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val senha = binding.etSenha.text.toString()

            if (login.isBlank() || senha.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            realizarLogin(login, senha)
        }
    }

    //a funcao envia o login para a API usando retrofit
    private fun realizarLogin(login: String, senha: String) {
        val request = LoginRequest(login, senha)
        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                // se der tudo certo, salva no sessionmanager e deois abre a tela de boas vindas
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        sessionManager.saveSession(
                            userId = loginResponse.id ?: 0,
                            login = login,
                            token = loginResponse.token
                        )
                        openBoasVindas()
                        //se der erro mostra a mensagem especifica 401 -> login ou senha errados
                        //400 dados de login invalidos
                    } else {
                        exibirErro("Falha na autenticação")
                    }
                } else {
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