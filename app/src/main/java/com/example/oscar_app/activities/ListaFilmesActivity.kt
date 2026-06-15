package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.oscar_app.adapters.FilmeAdapter
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ActivityListaFilmesBinding
import com.example.oscar_app.models.Filme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaFilmesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListaFilmesBinding
    private val apiService by lazy { OscarApiService.create() }

    // no oncreate ele configura a lista
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityListaFilmesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvFilmes.layoutManager = LinearLayoutManager(this)
        carregarFilmes()
    }

    // a função carregar filmes mostra uma barra de carregamento
    private fun carregarFilmes() {
        binding.progressBar.visibility = View.VISIBLE
        //depois chama a API
        apiService.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val filmes = response.body() ?: emptyList()
                    // quando filme chega ele cria o adapter
                    // ao clicar em um filme, ele abre filmeDetalheActivity e envia dados pelo Intent
                    // como ID, nome genero e foto
                    binding.rvFilmes.adapter = FilmeAdapter(filmes) { filme ->
                        val intent = Intent(this@ListaFilmesActivity, FilmeDetalheActivity::class.java)
                        intent.putExtra("FILME_ID", filme.id)
                        intent.putExtra("FILME_NOME", filme.nome)
                        intent.putExtra("FILME_GENERO", filme.genero)
                        intent.putExtra("FILME_FOTO", filme.foto)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@ListaFilmesActivity, "Erro ao carregar filmes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Filme>>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@ListaFilmesActivity, "Falha na conexão", Toast.LENGTH_SHORT).show()
            }
        })
    }
}