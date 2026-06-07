package com.example.oscar_app.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.oscar_app.databinding.ActivityFilmeDetalheBinding
import com.example.oscar_app.session.VoteManager

class FilmeDetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilmeDetalheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilmeDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("FILME_ID") ?: ""
        val nome = intent.getStringExtra("FILME_NOME") ?: ""
        val genero = intent.getStringExtra("FILME_GENERO") ?: ""
        val foto = intent.getStringExtra("FILME_FOTO") ?: ""

        binding.tvNomeDetalhe.text = nome
        binding.tvGeneroDetalhe.text = genero
        Glide.with(this).load(foto).into(binding.ivPosterDetalhe)

        if (VoteManager.voto.confirmado) {
            binding.btnVotarFilmeDetalhe.isEnabled = false
            binding.btnVotarFilmeDetalhe.text = "Voto Confirmado"
        }

        binding.btnVotarFilmeDetalhe.setOnClickListener {
            VoteManager.registrarFilme(id, nome)
            Toast.makeText(this, "Voto em $nome registrado localmente!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}