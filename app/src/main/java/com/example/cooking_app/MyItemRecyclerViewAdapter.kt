package com.example.cooking_app

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.cooking_app.Classi.Ingredienti

import com.example.cooking_app.databinding.FragmentItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private val values: ArrayList<Ingredienti>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nomeIng.text = item.Name
        holder.quantitIng.text = item.quantit + " " + item.misura
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val nomeIng: TextView = binding.TVNomeIngredienti
        val quantitIng: TextView = binding.TVQuantitaIngredienti

        override fun toString(): String {
            return super.toString() + " '" + quantitIng.text + "'"
        }
    }
}