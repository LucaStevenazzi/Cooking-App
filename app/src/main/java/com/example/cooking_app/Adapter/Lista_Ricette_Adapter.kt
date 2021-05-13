@file:Suppress("UNCHECKED_CAST")

package com.example.cooking_app.Adapter

import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.collections.ArrayList

/*
classe adattatatrice che permette di gestire la Lista (RecyclerView) delle ricette
 */
class Lista_Ricette_Adapter internal constructor(img: ArrayList<Ricetta>): RecyclerView.Adapter<Lista_Ricette_Adapter.CustomViewHolder>() , Filterable {

    private val TAG = "Lista_Ricette_Adapter"
    private val array : ArrayList<Ricetta> = img
    private val array_copy : ArrayList<Ricetta> = array
    private val DBStorage: StorageReference = FirebaseStorage.getInstance().getReference("Immagini")

    init{

    }


    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var cv : CardView = itemView.findViewById(R.id.cv_lista_ricette)
        var img_ricetta : ImageView = itemView.findViewById(R.id.img_ricetta)
        var titolo_ricetta : TextView = itemView.findViewById(R.id.titolo_ricetta)
        var difficoltà_ricetta : TextView = itemView.findViewById(R.id.tv_difficoltà_ricetta)
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //setting delle immagini e titoli delle ricette

        val immagine = array[position].immagine                                                     //prendo la stringa associata al campo immagine della ricetta contenente il nome dell'immagine
        val arrayPrefixSuffix = immagine.split(".")                                     //splitto la stringa presa per pttenere un prefisso (nome immagine) e un postfisso (formato)
        val localFile = File.createTempFile(arrayPrefixSuffix[0], arrayPrefixSuffix[1])             //creo un file temporaneo con le informazioni ottenute
        DBStorage.getFile(localFile)                                                                //cerco il file appena creato nello storage firebase
        holder.img_ricetta.setImageURI(Uri.fromFile(localFile));                                    //prendo il file ottenuto, lo casto ad Uri e lo setto nella ImageView della lista
        holder.titolo_ricetta.text = array[position].nome
        holder.tempo_ricetta.text = "Tempo : ${array[position].tempo}"
        holder.difficoltà_ricetta.text = "Difficoltà : ${array[position].diff}"

    }

    //metoco che restituisce il numero di Item nella lista delle ricette
    override fun getItemCount(): Int {
        return array.size
    }

    //estendo la classe putExtra con questo metodo per il salvataggio di ricette da un activity all'altra
    private fun putRicettaExtra(intent: Intent, ricetta: Ricetta) {

        intent.putExtra("Immagine", ricetta.immagine)
        intent.putExtra("Nome", ricetta.nome)
        intent.putExtra("Difficoltà", ricetta.diff)
        intent.putExtra("Tempo", ricetta.tempo)
        intent.putExtra("Tipologia", ricetta.tipologia)
        intent.putExtra("Portata", ricetta.portata)
        intent.putExtra("Persone", ricetta.persone)
        putIngredintiExtra(intent, ricetta.listaIngredienti)
        intent.putExtra("ListaIngredienti", ricetta.listaIngredienti)
        intent.putExtra("Note", ricetta.note)

    }

    //salvataggio nell'intent dei dati degli ingredienti
    private fun putIngredintiExtra(intent: Intent, listaIngredienti: ArrayList<Ingredienti>) {
        val count = listaIngredienti.size
        if(count == 0) return
        for( i in listaIngredienti.indices) {
            intent.putExtra("Ingrediente $i nome", listaIngredienti[i].Name)
            intent.putExtra("Ingrediente $i quantità", listaIngredienti[i].quantit)
            intent.putExtra("Ingrediente $i misura", listaIngredienti[i].misura)
        }
        intent.putExtra("Count", count)
    }

    //metodo per la il filtraggio della ricerca in base al testo che scrivi
    override fun getFilter(): Filter {
        return searchFilter
    }

    private var searchFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: ArrayList<Ricetta> = ArrayList()
            if (constraint.toString().isEmpty()) {
                filteredList.addAll(array_copy)
            } else {
                val filterPattern = constraint.toString().toLowerCase()
                for (item in array_copy) {
                    if (item.nome.toLowerCase().contains(filterPattern)) {
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
    }

}


