package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Classi.Ingredienti
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.view.*

// Activity per l'aggiunta delle ricette

class AddNewRecipeActivity : AppCompatActivity() {

    //dati
    var lista = arrayListOf<Ingredienti>()

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_recipe)

        setSpinner() //settaggio degli spinner per la visualizzazione delle PORTATE / DIFFICOLTà / MISURE


        //Codice per la lista degli ingredianti
        setRecyclerView()
        add_Ingrediente_to_List()

    }

    private fun setSpinner() {

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

        ArrayAdapter.createFromResource(        //contenitore dei valori della DropDown List per la difficoltà
                this,
                R.array.array_misure,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            ing_misura.adapter = adapter
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
    }

    private fun add_Ingrediente_to_List(): Boolean {
        add_ing.setOnClickListener(View.OnClickListener{ v ->
            var ingnome = ing_nome.text.toString()
            var ingquanti = ing_quantità.text.toString()
            var ingmisura = ing_misura.selectedItem.toString()
            if(ingnome.isEmpty()||ingquanti.isEmpty()||!ingquanti.isDigitsOnly()||ingnome.isDigitsOnly()){
                return@OnClickListener
            }
            var ing : Ingredienti = Ingredienti(ingnome, ingquanti,ingmisura)
            lista.add(0,ing)
            ing_nome.text.clear()
            ing_quantità.text.clear()
            recyclerview.adapter?.notifyItemInserted(0)
            return@OnClickListener
            }
        )
        return false
    }
    private fun setRecyclerView() {
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = Lista_Ingredienti_Adapter(lista)

    }

    fun saveRecipe(v : View) {      //funzione che salva i dati della ricetta

        val nome = ETnome.text.toString()
        val tempo = ETtempo.text.toString()
        val tipologia = ETtipologia.text.toString()
        val numPersone = ETpersone.text.toString()
        val diff = spinner_diff.selectedItem.toString()
        val portata = spinner_portata.selectedItem.toString()

    }
}