package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Locali_Adapter
import com.example.cooking_app.Classi.COL_DESC
import com.example.cooking_app.Classi.COL_NOME
import com.example.cooking_app.Classi.DataBaseHelper
import com.example.cooking_app.Classi.TABELLA_RICETTE
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*

//Activity per la visualizzazione delle proprie ricette in locale

class Lista_Ricette_Locali_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_ricette_locali)

        val db = DataBaseHelper(this)

        val lista = db.readData() //lettura del DB locale

        //inizializzazione della lista della ricette attraverso l'adapter

        val mAdapter = Lista_Ricette_Locali_Adapter(lista)
        lista_ricette_locali.layoutManager = LinearLayoutManager(this)
        lista_ricette_locali.adapter = mAdapter

    }
}