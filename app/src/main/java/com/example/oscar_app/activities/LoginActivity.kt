package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            openBoasVindas()
        }

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

    private fun realizarLogin(login: String, senha: String) {
        val request = LoginRequest(login, senha)
        apiService.login(request).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.success) {
                        sessionManager.saveSession(
                            userId = loginResponse.id ?: 0,
                            login = login,
                            token = loginResponse.token
                        )
                        openBoasVindas()
                    } else {
                        exibirErro("Login ou senha incorretos")
                    }
                } else {
                    exibirErro("Erro no servidor: ${response.code()}")
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