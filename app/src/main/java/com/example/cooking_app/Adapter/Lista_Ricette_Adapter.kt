package com.example.cooking_app.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.R
import com.example.cooking_app.View_Ricetta_Activity

/*
classe adattatatrice che permette di gestire la Lista (RecyclerView)
 */
class Lista_Ricette_Adapter(val img: ArrayList<Ricetta>): RecyclerView.Adapter<Lista_Ricette_Adapter.CustomViewHolder>() {

    private var array = img

    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var cv : CardView
        var img_ricetta : ImageView

        init{
            cv = itemView.findViewById(R.id.cv_lista_ricette)
            img_ricetta = itemView.findViewById(R.id.img_ricetta)

            //onClickListener: apertura nuova activity per la visualizzazione della ricetta cliccata come CardView
            //intent: passaggio dei dati
            cv.setOnClickListener {
                val intent = Intent(itemView.context, View_Ricetta_Activity::class.java)
                intent.putExtra("immagine", array[layoutPosition].immagine)
                array.clear()
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //setting delle immagini e titoli
        holder.img_ricetta.setImageResource(array[position].immagine)

    }

    //metoco che restituisce il numero di Item nella lista delle ricette
    override fun getItemCount(): Int {
        return array.size
    }

}