package com.example.cooking_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Adapter
import com.example.cooking_app.Classi.Ricetta
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_ricette_activity.*

/*
Main Activity con lista di ricette
 */

class List_Ricette_Activity : AppCompatActivity(){

    private val TAG = "List_Ricette_Activity"
    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference().child("ricette") //radice dell'albero per la View delle ricette
    private lateinit var mRicetteValueListener: ValueEventListener
    private var img: ArrayList<Ricetta> = ArrayList()
    private lateinit var mAdapter: Lista_Ricette_Adapter

    private lateinit var toggle: ActionBarDrawerToggle

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)

        setCompopnent()

    }

    //settaggio dei componenti
    private fun setCompopnent() {
        initBarMenuLateral() //inizializzazione Barra laterale del menu
    }
    private fun initBarMenuLateral() {
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        navView.setNavigationItemSelectedListener{
            when(it.itemId){
                R.id.miItem1 -> Toast.makeText(applicationContext, "Clicked Item 1", Toast.LENGTH_SHORT).show()
                R.id.miItem2 -> Toast.makeText(applicationContext, "Clicked Item 2", Toast.LENGTH_SHORT).show()
                R.id.miItem3 -> Toast.makeText(applicationContext, "Clicked Item 3", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }
    private fun initRecyclerView() {
        mAdapter = Lista_Ricette_Adapter(img, this)
        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = mAdapter
    }

    //OnClick: apertura nuova activity per l'aggiunta di una ricetta
    fun newRecipe(v: View) {
        val it = Intent(this, AddNewRecipeActivity::class.java)
        startActivity(it)
    }

    //Settaggio ToolBar
    override fun onCreateOptionsMenu(menu: Menu):Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val search = menu.findItem(R.id.search_icon)
        val filter = menu.findItem(R.id.search_filter)
        val carrello = menu.findItem(R.id.carrello)


        search.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                carrello.isVisible = false
                filter.isVisible = true
                search.isVisible = false
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {  //fine della ricerca inizializza la OptionMenu
                invalidateOptionsMenu()
                return true
            }
        })
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {//selezione del funzione dell'OptionMenu
        return when(item.itemId){
            R.id.search_filter -> {
                //fragment per la scelta del filtro della ricerca
                applicaFiltro()
                true
            }
            R.id.search_icon -> {
                search(item)
                true
            }
            R.id.carrello -> {
                //start fragment carrello

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun applicaFiltro() {

    }

    //ricerca
    private fun search(item: MenuItem) {
        val searchItem = item.actionView as SearchView
        searchItem.imeOptions = EditorInfo.IME_ACTION_DONE //cambio pulsante della tastiera da search a conferma
        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //controlla nell'array di ricette
                mAdapter.filter.filter(newText)
                return true
            }
        })
    }

    //lettura dei dati da Firebase
    override fun onStart() {
        super.onStart()
        mRicetteValueListener = getDataToFireBase()   //visulaizza i dati delle ricette
        DBricette!!.addValueEventListener(mRicetteValueListener)         //aggiungiamo il listener degli eventi  per la lettura dei dati sul riferimento al DB
    }
    override fun onStop() {
        Log.e(TAG,"onStop")
        super.onStop()
        DBricette!!.removeEventListener(mRicetteValueListener)
    }
    private fun getDataToFireBase(): ValueEventListener{ //prima lettura dei dati dal Database o anche modifica dei Dati
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                img.clear()
                for (ds in dataSnapshot.children) {
                    val ricetta: Ricetta? = ds.getValue(Ricetta::class.java)
                    img.add(ricetta!!)
                }
                initRecyclerView() //inizializzazione Lista delle ricette
                mAdapter.notifyDataSetChanged() //serve per l'upgrada della lista delle ricette
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Ricetta failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                mAdapter.notifyDataSetChanged()
            }
        }
        return postListener
    }

}

