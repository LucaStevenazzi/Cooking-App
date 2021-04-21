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
    //private var adapterLV : ArrayAdapter<Ingredienti> = ArrayAdapter(this, android.R.layout.simple_list_item_1, arraylist_ing)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la difficoltà
                this,
                R.array.array_diff,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_diff.adapter = adapter
        }

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la portata
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

        /*val ingr = Ingredienti("sale", null , null)
        arraylist_ing.add(ingr)
        LVlistaIng.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, arraylist_ing)*/

    }

    fun saveRecipe(v : View) {      //funzione che salva i dati della ricetta

        val nome = ETnome.text.toString()
        val diff = spinner_diff.selectedItem.toString()
        val tempo = ETtempo.text.toString()
        val tipologia = ETtipologia.text.toString()
        val portata = spinner_portata.selectedItem.toString()
        val numPersone = ETpersone.text.toString().toInt()
        arraylist_note.add(ETnote.text.toString())

        val ricetta = Recipe(nome, diff, tempo, tipologia, portata, numPersone, arraylist_ing, arraylist_note)
        Log.v("oggetto", ricetta.toString())
        Toast.makeText(this,"Aggiunta la ricetta: $nome",Toast.LENGTH_LONG).show();
        finish()
    }

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