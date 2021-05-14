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
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Adapter
import com.example.cooking_app.Classi.Ricetta
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_ricette_activity.*
import kotlin.collections.ArrayList


/*
Main Activity con lista di ricette
 */

@Suppress("UNREACHABLE_CODE")
class List_Ricette_Activity : AppCompatActivity(){

    private val TAG = "List_Ricette_Activity"

    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference().child("ricette") //radice dell'albero per la View delle ricette
    private lateinit var mRicetteValueListener: ValueEventListener
    private lateinit var img: ArrayList<Ricetta>
    private lateinit var mAdapter: Lista_Ricette_Adapter

    private lateinit var toggle: ActionBarDrawerToggle

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)

        initData()

    }

    private fun initData() {
        mRicetteValueListener = getDataToFireBase()   //visulaizza i dati delle ricette
        //img?.clear() //cancello la lista delle ricette per non aggiungerle piu volte nel list_ricette = RecyclerView
        DBricette!!.addValueEventListener(mRicetteValueListener) //aggiungiamo il listener degli eventi  per la lettura dei dati sul riferimento al DB
        initRecyclerView() //inizializzazione Lista delle ricette
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
        lista_ricette.setHasFixedSize(true)
        mAdapter = Lista_Ricette_Adapter(img)
        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = mAdapter
    }
    private fun getDataToFireBase(): ValueEventListener{ //prima lettura dei dati dal Database o anche modifica dei Dati
        img = ArrayList()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val ricetta: Ricetta? = ds.getValue(Ricetta::class.java)
                    img.add(ricetta!!)
                }
                mAdapter.notifyDataSetChanged() //serve per l'upgrada della lista delle ricette
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Ricetta failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                mAdapter.notifyDataSetChanged()
            }
        }
        return postListener
    } //lettura dei dati da Firebase

    //OnClick: apertura nuova activity per l'aggiunta di una ricetta
    fun newRecipe(v: View) {
        val it = Intent(this, AddNewRecipeActivity::class.java)
        startActivity(it)
    }

    //Ricerca
    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        menuInflater.inflate(R.menu.search, menu)
        val searchItem = menu.findItem(R.id.search_icon)
        val searchView = searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE //cambio pulsante della tastiera da search a conferma
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                //controlla nell'array di ricette
                mAdapter.filter.filter(newText)
                return false
            }
        })
        return true
    }

    //selezione del funzione della MenuBar laterale
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        DBricette!!.removeEventListener(mRicetteValueListener)
    }
}

