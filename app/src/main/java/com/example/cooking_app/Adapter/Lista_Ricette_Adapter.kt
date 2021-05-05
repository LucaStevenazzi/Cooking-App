package com.example.cooking_app.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.R
import com.example.cooking_app.View_Ricetta_Activity

/*
classe adattatatrice che permette di gestire la Lista (RecyclerView)
 */
class Lista_Ricette_Adapter(val img: ArrayList<Ricetta>): RecyclerView.Adapter<Lista_Ricette_Adapter.CustomViewHolder>() {

    private val TAG = "Lista_Ricette_Adapter"
    private var array = img

    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var cv : CardView = itemView.findViewById(R.id.cv_lista_ricette)
        var img_ricetta : ImageView = itemView.findViewById(R.id.img_ricetta)
        var titolo_ricetta : TextView = itemView.findViewById(R.id.titolo_ricetta)

        init{

            //onClickListener: apertura nuova activity per la visualizzazione della ricetta cliccata come CardView
            //intent: passaggio dei dati
            cv.setOnClickListener {
                val intent = Intent(itemView.context, View_Ricetta_Activity::class.java)
                putRicettaExtra(intent, array[layoutPosition])   //passaggio ddlla ricetta cliccata dall elenco tramite l'intent
                itemView.context.startActivity(intent)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //setting delle immagini e titoli delle ricette
        holder.img_ricetta.setImageResource(array[position].immagine)
        holder.titolo_ricetta.text = array[position].nome
    }

    //metoco che restituisce il numero di Item nella lista delle ricette
    override fun getItemCount(): Int {
        return array.size
    }

}

//estendo la classe putExtra con questo metodo per il salvataggio di ricette da un activity all'altra
private fun putRicettaExtra(intent: Intent, ricetta: Ricetta) {

    intent.putExtra("Immagine" , ricetta.immagine)
    intent.putExtra("Nome" , ricetta.nome)
    intent.putExtra("Difficoltà" , ricetta.diff)
    intent.putExtra("Tempo" , ricetta.tempo)
    intent.putExtra("Tipologia" , ricetta.tipologia)
    intent.putExtra("Portata" , ricetta.portata)
    intent.putExtra("Persone" , ricetta.persone)
    intent.putExtra("ListaIngredienti" , ricetta.listaIngredienti)
    intent.putExtra("Note" , ricetta.note)

}
