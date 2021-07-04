package com.example.cooking_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.*
import com.example.cooking_app.Classi.Ingredienti

//Classe adapter che gestisce la lista degli ingredienti
class Lista_Ingredienti_Adapter(lista: ArrayList<Ingredienti>) : RecyclerView.Adapter<Lista_Ingredienti_Adapter.ViewHolder>() {

    private var array : ArrayList<Ingredienti> = lista

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var cv : CardView? = null
        var itemNomeIngrediente: TextView
        var itemQuantitIngrediente: TextView
        var itemDelete: ImageView? = null

        init{
            if(itemView.context is AddNewRecipeActivity || itemView.context is Lista_Spesa){
                cv = itemView.findViewById(R.id.cv_lista_ingredienti)
                itemDelete = itemView.findViewById(R.id.img_delete)
                itemNomeIngrediente = itemView.findViewById(R.id.list_ing_name)
                itemQuantitIngrediente = itemView.findViewById(R.id.list_view_ing_quantità)

                //controllo premuta icona per l'eliminazione degli ingredienti
                itemDelete?.setOnClickListener{ v: View  ->
                    array.removeAt(bindingAdapterPosition)
                    notifyItemRemoved(bindingAdapterPosition)
                    notifyDataSetChanged()
                }
            }else{
                itemNomeIngrediente = itemView.findViewById(R.id.list_view_ing_name)
                itemQuantitIngrediente = itemView.findViewById(R.id.list_view_ing_quantità)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(parent.context).inflate(R.layout.card_ingrediente, parent, false)
        if(parent.context is View_Ricetta_Activity)
        v = LayoutInflater.from(parent.context).inflate(R.layout.card_view_ingrediente, parent, false)
        return ViewHolder(v)
    }

    //gestione della visualizzazione dei dati dell'ingrediente nella lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemNomeIngrediente.text = array[position].Name
        holder.itemQuantitIngrediente.text = array[position].quantit.plus(" ").plus(array[position].misura)
    }

    override fun getItemCount(): Int {
        return array.size
    }

}
