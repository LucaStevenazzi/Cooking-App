package com.example.cooking_app.Adapter

import android.content.Intent
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

//Adapter per gestire la recycler view delle ricette in locale

class Lista_Ricette_Locali_Adapter(img: ArrayList<Ricetta>) : RecyclerView.Adapter<Lista_Ricette_Locali_Adapter.CustomViewHolder>(){

    private val array : ArrayList<Ricetta> = img
    private lateinit var ricette : Ricetta

    //classe interna che aggiunge un listener ad ogni ricetta, il quale apre l'activity per la sua visualizzazione
    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){//classe che gestisce le View della RecycleView

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

    }

    //funzione che permette di usare il layout che gestisce i singoli elementi della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }

    //funzione che associa al layout appena preso i valori che deve mostrare
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        //inserire il codice per settare l'immagine
        holder.titolo_ricetta.text = array[position].nome
        val tempo =  "Tempo : ${array[position].tempo}"
        holder.tempo_ricetta.text = tempo
        val diff = "Difficoltà : ${array[position].diff}"
        holder.difficolta_ricetta.text = diff
    }

    //funzinoe che restituisce il numero di elementi dell'array di ricette passato al costruttore
    override fun getItemCount(): Int {
        return array.size
    }

    //passaggio tramite intent della ricetta selezionata
    private fun putRicettaExtra(intent: Intent, ricetta: Ricetta) {//inserisco nell'intent i valori della ricetta che è stata cliccata
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
    }
    //passaggio degli ingredienti
    private fun putIngredintiExtra(intent: Intent) {//salvataggio nell'intent dei dati degli ingredienti
        val count = ricette.listaIngredienti.size
        if(count == 0) return
        for( i in ricette.listaIngredienti.indices) {
            intent.putExtra("Ingrediente $i nome", ricette.listaIngredienti[i].Name)
            intent.putExtra("Ingrediente $i quantità", ricette.listaIngredienti[i].quantit)
            intent.putExtra("Ingrediente $i misura", ricette.listaIngredienti[i].misura)
        }
        intent.putExtra("Count", count)
    }

}