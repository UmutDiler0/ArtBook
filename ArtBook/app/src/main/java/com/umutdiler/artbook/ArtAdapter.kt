package com.umutdiler.artbook

import android.content.Intent
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

import android.view.ViewGroup
import com.umutdiler.artbook.databinding.RecyclerrowBinding


class ArtAdapter(val arrayArt:ArrayList<ArtInfo>) : RecyclerView.Adapter<ArtAdapter.ArtHolder>() {

    class ArtHolder(val binding : RecyclerrowBinding) : RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ArtHolder {
        val binding = RecyclerrowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ArtHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtHolder, position: Int) {
        holder.binding.textView.text = arrayArt.get(position).name
        holder.itemView.setOnClickListener({
            val intent = Intent(holder.itemView.context,ArtInfo::class.java)
            holder.itemView.context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return arrayArt.size
    }
}