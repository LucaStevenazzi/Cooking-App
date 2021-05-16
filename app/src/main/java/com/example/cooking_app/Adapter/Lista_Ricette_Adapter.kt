package com.example.cooking_app.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.R
import com.example.cooking_app.View_Ricetta_Activity
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

/*
classe adattatatrice che permette di gestire la Lista (RecyclerView) delle ricette
 */
class Lista_Ricette_Adapter internal constructor(img: ArrayList<Ricetta>, context : Context): RecyclerView.Adapter<Lista_Ricette_Adapter.CustomViewHolder>() , Filterable {

    private val TAG = "Lista_Ricette_Adapter"
    private val array : ArrayList<Ricetta> = img
    private val arrayCopy : ArrayList<Ricetta> = array
    private lateinit var ricette : Ricetta
    private val ct = context

    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private var cv : CardView = itemView.findViewById(R.id.cv_lista_ricette)
        var img_ricetta : ImageView = itemView.findViewById(R.id.img_ricetta)
        var titolo_ricetta : TextView = itemView.findViewById(R.id.titolo_ricetta)
        var difficolta_ricetta : TextView = itemView.findViewById(R.id.tv_difficoltà_ricetta)
        var tempo_ricetta : TextView = itemView.findViewById(R.id.tv_tempo_ricetta)

        init{

            //onClickListener: apertura nuova activity per la visualizzazione della ricetta cliccata come CardView
            //intent: passaggio dei dati
            cv.setOnClickListener {
                val intent = Intent(itemView.context, View_Ricetta_Activity::class.java)
                putRicettaExtra(intent, array[layoutPosition])   //passaggio ddlla ricetta cliccata dall elenco tramite l'intent
                it.context.startActivity(intent)
            }
        }

    }//classe che gestisce le View della RecycleView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }//assegnazione del tipo di layout all'Hoder della RecycleView
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //setting delle immagini e titoli delle ricette
        Picasso.with(ct).load(array[position].immagine).into(holder.img_ricetta)
        holder.titolo_ricetta.text = array[position].nome
        val tempo =  "Tempo : ${array[position].tempo}"
        holder.tempo_ricetta.text = tempo
        val diff = "Difficoltà : ${array[position].diff}"
        holder.difficolta_ricetta.text = diff

    }//assegna i dati alle righe della RecycleView
    override fun getItemCount(): Int {
        return array.size
    }//metoco che restituisce il numero di Item nella lista delle ricette

    //passaggio tramite intent della ricetta selezionata
    private fun putRicettaExtra(intent: Intent, ricetta: Ricetta) {
        ricette = ricetta
        intent.putExtra("Immagine", ricette.immagine)
        intent.putExtra("Nome", ricette.nome)
        intent.putExtra("Difficoltà", ricette.diff)
        intent.putExtra("Tempo", ricette.tempo)
        intent.putExtra("Tipologia", ricette.tipologia)
        intent.putExtra("Portata", ricette.portata)
        intent.putExtra("Persone", ricette.persone)
        putIngredintiExtra(intent)
        intent.putExtra("ListaIngredienti", ricette.listaIngredienti)
        intent.putExtra("Note", ricette.note)

    } //inserisco nell'intent i valori della ricetta che è stata cliccata
    private fun putIngredintiExtra(intent: Intent) {
        val count = ricette.listaIngredienti.size
        if(count == 0) return
        for( i in ricette.listaIngredienti.indices) {
            intent.putExtra("Ingrediente $i nome", ricette.listaIngredienti[i].Name)
            intent.putExtra("Ingrediente $i quantità", ricette.listaIngredienti[i].quantit)
            intent.putExtra("Ingrediente $i misura", ricette.listaIngredienti[i].misura)
        }
        intent.putExtra("Count", count)
    }//salvataggio nell'intent dei dati degli ingredienti

    //ricerca
    private var searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<Ricetta> = ArrayList()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(arrayCopy)
            } else {
                val filterPattern = constraint.toString().toLowerCase(Locale.ROOT)
                for (item in arrayCopy) {
                    if (item.nome.toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            results.count = filteredList.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            array.clear()
            array.addAll(results.values as List<Ricetta>)
            notifyDataSetChanged()
        }
    } //variabile filtro per nome nella ricerca
    override fun getFilter(): Filter {
        return searchFilter
    }//metodo per la il filtraggio della ricerca in base al testo che scrivi



}


