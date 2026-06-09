package com.example.oscar_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oscar_app.api.OscarApiService
import com.example.oscar_app.databinding.ItemFilmeBinding
import com.example.oscar_app.models.Filme

class FilmeAdapter(
    private val filmes: List<Filme>,
    private val onItemClick: (Filme) -> Unit
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>() {

    class FilmeViewHolder(val binding: ItemFilmeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmeViewHolder {
        val binding = ItemFilmeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FilmeViewHolder, position: Int) {
        val filme = filmes[position]
        holder.binding.tvNomeFilme.text = filme.nome
        holder.binding.tvGeneroFilme.text = filme.genero

        val urlCompleta = OscarApiService.BASE_URL + filme.foto

        Glide.with(holder.itemView.context)
            .load(urlCompleta)
            .into(holder.binding.ivPoster)

        holder.itemView.setOnClickListener { onItemClick(filme) }
    }

    override fun getItemCount(): Int = filmes.size
}