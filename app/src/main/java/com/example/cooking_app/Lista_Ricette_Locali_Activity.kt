package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Locali_Adapter
import com.example.cooking_app.Classi.*
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*

//Activity per la visualizzazione delle proprie ricette in locale
class Lista_Ricette_Locali_Activity : AppCompatActivity() {

    private lateinit var mAdapter : Lista_Ricette_Locali_Adapter
    private val db = DataBaseHelper(this)
    private lateinit var lista: ArrayList<Ricetta>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_ricette_locali)

        lista = db.readData() //lettura del DB locale

        //inizializzazione della lista della ricette attraverso l'adapter

        mAdapter = Lista_Ricette_Locali_Adapter(lista)
        lista_ricette_locali.layoutManager = LinearLayoutManager(this)
        lista_ricette_locali.adapter = mAdapter

    }

    override fun onRestart() {
        super.onRestart()
        checkModifiche()//controllo cambiamenti

    }

    //funzione che aggiorna la lista dopo eventuali modifiche alle ricette
    private fun checkModifiche() {
        lista.clear()
        lista.addAll(db.readData())
        mAdapter.notifyDataSetChanged()
    }
}