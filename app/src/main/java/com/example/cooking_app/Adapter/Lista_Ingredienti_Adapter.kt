package com.example.cooking_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.R

class Lista_Ingredienti_Adapter(lista: ArrayList<Ingredienti>) : RecyclerView.Adapter<Lista_Ingredienti_Adapter.ViewHolder>() {

    private var array = lista

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cv : CardView
        var itemNomeIngrediente: TextView
        var itemQuantitàIngrediente: TextView
        var itemDelete: ImageView


        init{
            cv = itemView.findViewById(R.id.cv_lista_ingredienti)
            itemDelete = itemView.findViewById(R.id.img_delete)
            itemNomeIngrediente = itemView.findViewById(R.id.list_ing_name)
            itemQuantitàIngrediente = itemView.findViewById(R.id.list_ing_quantità)

            //controllo premuta icona com.example.cooking_app.di delete
            itemDelete.setOnClickListener{ v: View  ->
                array.removeAt(bindingAdapterPosition)
                notifyItemRemoved(bindingAdapterPosition)
                notifyDataSetChanged()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_ingrediente, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemNomeIngrediente.text = array[position].Name
        holder.itemQuantitàIngrediente.text = array[position].quantit +" " + array[position].misura
    }

    override fun getItemCount(): Int {
        return array.size
    }

}
