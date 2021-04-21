package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_add_new_recipe.*

// Activity per l'aggiunta delle ricette

class AddNewRecipeActivity : AppCompatActivity(), View.OnKeyListener {
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
    }

    fun saveRecipe(v : View) {      //funzione che salva i dati della ricetta

        val nome = ETnome.text.toString()
        val tempo = ETtempo.text.toString()
        val tipologia = ETtipologia.text.toString()
        val numPersone = ETpersone.text.toString()
        val diff = spinner_diff.selectedItem.toString()
        val portata = spinner_portata.selectedItem.toString()

    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
            val ing = ETingredienti.text.toString()
            val string : String

            if(ing == ""){
                val b = ETlistaIng.text.toString()      //rimane uno spazio vuoto quando inserisco il primo ingrediente, si può sistemare anche dopo
                string = b
            }else{
                val b = ETlistaIng.text.toString()
                string = ing + "\n" + b
            }

            ETlistaIng.setText(string)
            ETingredienti.setText("")

            //crea arraylist per salvare la lista
            //chiedi al prof del .clear
            return true
        }
        return false
    }

}