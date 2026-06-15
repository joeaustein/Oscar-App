//filme adapter é o codiggo qeu transforma cada objeto do FILME em um item visual da lista
package com.example.oscar_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.oscar_app.api.NetworkConfig
import com.example.oscar_app.databinding.ItemFilmeBinding
import com.example.oscar_app.models.Filme

class FilmeAdapter(
    private val filmes: List<Filme>, // ele recebe
    private val onItemClick: (Filme) -> Unit // ele recebe
) : RecyclerView.Adapter<FilmeAdapter.FilmeViewHolder>() {

    class FilmeViewHolder(val binding: ItemFilmeBinding) : RecyclerView.ViewHolder(binding.root)

    //ele infla o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmeViewHolder {
        val binding = ItemFilmeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmeViewHolder(binding)
    }
    // ele preenche os dados do filme
    override fun onBindViewHolder(holder: FilmeViewHolder, position: Int) {
        val filme = filmes[position]
        holder.binding.tvNomeFilme.text = filme.nome
        holder.binding.tvGeneroFilme.text = filme.genero

        val urlCompleta = if (filme.foto.startsWith("http")) {
            filme.foto
        } else {
            NetworkConfig.BASE_URL + filme.foto
        }

        //para a imagem, ele monta a URL e usa glide
        Glide.with(holder.itemView.context)
            .load(urlCompleta)
            .into(holder.binding.ivPoster)

        //quando usuario clica no item
        holder.itemView.setOnClickListener { onItemClick(filme) }
    }

    override fun getItemCount(): Int = filmes.size
}