package com.example.cooking_app

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Adapter
import com.example.cooking_app.Listener.onClickListener
import kotlinx.android.synthetic.main.list_ricette_activity.*


/*
Main Activity con lista di ricette
 */

class List_Ricette_Activity : AppCompatActivity() , onClickListener {

    //array di ricette
    private  val img = arrayListOf(
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3,
            R.drawable.img_4, R.drawable.img_5, R.drawable.img_6)

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)



        initRecyclerView() //inizializzazione Lista delle ricette
    }

    private fun initRecyclerView() {

        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = Lista_Ricette_Adapter(img, this)

    }


    //onClickListener: apertura nuova activity per la visualizzazione della ricetta cliccata
    //intent: passaggio dei dati
    override fun onClickListenerItem(position: Int) {
        val intent = Intent(this, View_Ricetta_Activity::class.java)
        intent.putExtra("immagine", img[position])
        startActivity(intent)
    }

    //OnClick: apertura nuova activity per l'aggiunta di una ricetta
    fun newRecipe(v: View) {
        val it = Intent(this, AddNewRecipeActivity::class.java)
        startActivity(it)
    }

    //Codice per il tasto della ricerca
    override fun onCreateOptionsMenu(menu: Menu?):Boolean{
        val inflater = menuInflater
        inflater.inflate(R.menu.search, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()
                Toast.makeText(this@List_Ricette_Activity, "Looking for $query", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }
}

