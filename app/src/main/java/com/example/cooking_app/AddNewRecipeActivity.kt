package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_new_recipe.*

// Activity per l'aggiunta delle ricette

class AddNewRecipeActivity : AppCompatActivity(), View.OnKeyListener {
    private var arraylist_ing : ArrayList<Ingredienti> = arrayListOf()
    private var arraylist_note : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        //contenitore dei valori della DropDown List per la difficoltà
        ArrayAdapter.createFromResource(
                this,
                R.array.array_diff,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_diff.adapter = adapter
        }

        //contenitore dei valori della DropDown List per la portata
        ArrayAdapter.createFromResource(
                this,
                R.array.array_portata,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_portata.adapter = adapter
        }

        ETingredienti.setOnKeyListener(this)
    }

    //funzione che salva i dati della ricetta
    fun saveRecipe(v : View) {

        val nome = ETnome.text.toString()
        val diff = spinner_diff.selectedItem.toString()
        val tempo = ETtempo.text.toString()
        val tipologia = ETtipologia.text.toString()
        val portata = spinner_portata.selectedItem.toString()
        val numPersone = ETpersone.text.toString().toInt()
        arraylist_note.add(ETnote.text.toString())

        val ricetta = Recipe(nome, diff, tempo, tipologia, portata, numPersone, arraylist_ing, arraylist_note)
        //Log.v("oggetto", ricetta.toString())
        Toast.makeText(this,"Aggiunta la ricetta: $nome",Toast.LENGTH_LONG).show();
        //chiusura activity dell'aggiunta di una ricetta e apertura activity principale
        finish()
    }

    //funzione che premendo invio ("ENTER") aggiunge l'ingrediente ad un arraylist e poi lo cancella dell'edittext
    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {

            val ing = ETingredienti.text.toString()
            val ingr = Ingredienti(ing, null , null)
            arraylist_ing.add(ingr)
            ETingredienti.text.clear()
            return true
        }
        return false
    }

}