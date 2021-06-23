package com.example.cooking_app.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.R
import com.example.cooking_app.View_Ricetta_Activity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*
import java.io.ByteArrayOutputStream
import kotlin.random.Random


//Adapter per gestire la recycler view delle ricette in locale

class Lista_Ricette_Locali_Adapter(img: ArrayList<Ricetta>) : RecyclerView.Adapter<Lista_Ricette_Locali_Adapter.CustomViewHolder>(){

    private val array : ArrayList<Ricetta> = img
    private lateinit var ricette : Ricetta
    private lateinit var imageUri: Uri

    //classe interna che aggiunge un listener ad ogni ricetta, il quale apre l'activity per la sua visualizzazione
    inner class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){//classe che gestisce le View della RecycleView

        private var cv : CardView = itemView.findViewById(R.id.cv_lista_ricette)
        var img_ricetta : ImageView = itemView.findViewById(R.id.img_ricetta)       //va usato quando si prende l'immagine da DB
        var titolo_ricetta : TextView = itemView.findViewById(R.id.titolo_ricetta)
        var difficolta_ricetta : TextView = itemView.findViewById(R.id.tv_difficoltà_ricetta)
        var tempo_ricetta : TextView = itemView.findViewById(R.id.tv_tempo_ricetta)
        var button_upload : ImageButton? = itemView.findViewById(R.id.uploadtoFireBase)

        init{

            //onClickListener: apertura nuova activity per la visualizzazione della ricetta cliccata come CardView
            //intent: passaggio dei dati
            cv.setOnClickListener {
                val intent = Intent(itemView.context, View_Ricetta_Activity::class.java)
                putRicettaLocaleExtra(intent, array[layoutPosition])   //passaggio ddlla ricetta cliccata dall elenco tramite l'intent
                it.context.startActivity(intent)
            }
            button_upload?.setOnClickListener {

                ricette = array[layoutPosition]
                val DBricette: DatabaseReference = FirebaseDatabase.getInstance().getReference("ricette")
                val DBStorage: StorageReference = FirebaseStorage.getInstance().getReference("Immagini")
                val fileReference = DBStorage.child(ricette.immagine)
                val imageUri = Uri.parse(convertImage(ricette.bit!!).toString())
                //funzioni che permettono di svolgere azioni quando l'upload è avvenuto con successo, quando fallisce e quando sta caricando
                fileReference.putFile(imageUri).addOnSuccessListener {
                    //salvataggio degli ingredienti sul DB
                    DBricette.child(ricette.immagine).setValue(ricette)
                    Toast.makeText(button_upload!!.context, "Aggiunta: ${ricette.nome}", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    //mostra l'errore
                    e-> Toast.makeText(button_upload!!.context, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    //funzione che restituisce l'array di byte relativo all'immagine passata per argomento
    private fun convertImage(image : Bitmap) : ByteArray{
        val objByteArrayOutputStream = ByteArrayOutputStream()      //nel DB non si poò salvare una bitmap, va quindi prima convertita in un ByteArrayOutputStream
        image.compress(Bitmap.CompressFormat.JPEG, 100, objByteArrayOutputStream)       //si converte la bitmap in un ByteArrayOutputStream
        val imageInBytes : ByteArray = objByteArrayOutputStream.toByteArray()       //si inseriscono i byte in un array
        return imageInBytes
    }

    //funzione che permette di usare il layout che gestisce i singoli elementi della lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list_locale,parent,false))
    }

    //funzione che associa al layout appena preso i valori che deve mostrare
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.titolo_ricetta.text = array[position].nome
        holder.img_ricetta.setImageBitmap(Bitmap.createScaledBitmap(array[position].bit!!, 200, 200, false))
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
    fun putRicettaLocaleExtra(intent: Intent, ricetta: Ricetta) {//inserisco nell'intent i valori della ricetta che è stata cliccata
        ricette = ricetta
        //conversione immagine in bytearray
        val objByteArrayOutputStream = ByteArrayOutputStream()
        ricette.bit!!.compress(Bitmap.CompressFormat.JPEG, 100, objByteArrayOutputStream)
        val imageInBytes : ByteArray = objByteArrayOutputStream.toByteArray()

        intent.putExtra("Bitmap", imageInBytes)
        intent.putExtra("Immagine", ricette.immagine)
        intent.putExtra("Nome", ricette.nome)
        intent.putExtra("Difficoltà", ricette.diff)
        intent.putExtra("Tempo", ricette.tempo)
        intent.putExtra("Tipologia", ricette.tipologia)
        intent.putExtra("Portata", ricette.portata)
        intent.putExtra("Persone", ricette.persone)
        LocaliputIngredintiExtra(intent)
        intent.putExtra("ListaIngredienti", ricette.listaIngredienti)
        intent.putExtra("Note", ricette.note)
    }
    //passaggio degli ingredienti
    private fun LocaliputIngredintiExtra(intent: Intent) {//salvataggio nell'intent dei dati degli ingredienti
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