package com.example.cooking_app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Locali_Adapter
import com.example.cooking_app.Classi.*
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*

//Activity per la visualizzazione delle proprie ricette in locale

class Lista_Ricette_Locali_Activity : AppCompatActivity() {

    private lateinit var mAdapter : Lista_Ricette_Locali_Adapter
    private val db = DataBaseHelper(this)
    private lateinit var lista: ArrayList<Ricetta>
    private var lista_ingredienti = ArrayList<Ingredienti>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_ricette_locali)
        lista = db.readData() //lettura del DB locale

        //inizializzazione della lista della ricette attraverso l'adapter

        mAdapter = Lista_Ricette_Locali_Adapter(lista, this)
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

    private val ADD_SPESA = 100
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_SPESA && resultCode == Activity.RESULT_OK){
            Toast.makeText(this, "Ricevuto indietro la lista degli ingredienti", Toast.LENGTH_SHORT).show()
            lista_ingredienti.addAll(data?.getSerializableExtra("lista spesa") as ArrayList<Ingredienti>)
            intent.putExtra("lista spesa",lista_ingredienti)
            setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }
}