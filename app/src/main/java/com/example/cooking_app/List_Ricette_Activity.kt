package com.example.cooking_app

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.R
import kotlinx.android.synthetic.main.list_ricette_activity.*

/*
Main Activity con lista di ricette
 */

class List_Ricette_Activity : AppCompatActivity() , onClickListener{

    private  val img = arrayOf(
            R.drawable.img_1,R.drawable.img_2,R.drawable.img_3,
            R.drawable.img_4,R.drawable.img_5,R.drawable.img_6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)

        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = CustomAdapter(img,this)
    }

    override fun onClickListenerItem(position: Int) {
        val intent = Intent(this, View_Ricetta_Activity::class.java)
        intent.putExtra("immagine" , img[position])
        startActivity(intent)
    }

    //Codice per il tasto della ricerca
    override fun onCreateOptionsMenu(menu: Menu?):Boolean{
        val inflater = menuInflater
        inflater.inflate(R.menu.search, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                searchView.clearFocus()
                searchView.setQuery("",false)
                searchItem.collapseActionView()
                Toast.makeText(this@List_Ricette_Activity, "Looking for $query", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean{
                return false
            }
        })
        return true
    }
}
