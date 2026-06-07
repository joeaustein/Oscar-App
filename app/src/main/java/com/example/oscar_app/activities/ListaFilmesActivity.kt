package com.example.oscar_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaFilmesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvFilmes.layoutManager = LinearLayoutManager(this)
        carregarFilmes()
    }

    private fun carregarFilmes() {
        binding.progressBar.visibility = View.VISIBLE
        apiService.getFilmes().enqueue(object : Callback<List<Filme>> {
            override fun onResponse(call: Call<List<Filme>>, response: Response<List<Filme>>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val filmes = response.body() ?: emptyList()
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