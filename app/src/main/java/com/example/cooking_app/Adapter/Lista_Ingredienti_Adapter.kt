package com.example.cooking_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.AddNewRecipeActivity
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.R
import com.example.cooking_app.View_Ricetta_Activity

class Lista_Ingredienti_Adapter(lista: ArrayList<Ingredienti>) : RecyclerView.Adapter<Lista_Ingredienti_Adapter.ViewHolder>() {

    private var array : ArrayList<Ingredienti> = lista

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cv : CardView? = null
        var itemNomeIngrediente: TextView
        var itemQuantitàIngrediente: TextView
        var itemDelete: ImageView? = null

        init{
            if(itemView.context is AddNewRecipeActivity){
                cv = itemView.findViewById(R.id.cv_lista_ingredienti)
                itemDelete = itemView.findViewById(R.id.img_delete)
                itemNomeIngrediente = itemView.findViewById(R.id.list_ing_name)
                itemQuantitàIngrediente = itemView.findViewById(R.id.list_view_ing_quantità)

                //controllo premuta icona com.example.cooking_app.di delete
                itemDelete?.setOnClickListener{ v: View  ->
                    array.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)
                    notifyDataSetChanged()
                }
            }else{
                itemNomeIngrediente = itemView.findViewById(R.id.list_view_ing_name)
                itemQuantitàIngrediente = itemView.findViewById(R.id.list_view_ing_quantità)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.card_ingrediente, parent, false)
        if(parent.context is View_Ricetta_Activity)
            v = LayoutInflater.from(parent.context).inflate(R.layout.card_view_ingrediente, parent, false)
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
