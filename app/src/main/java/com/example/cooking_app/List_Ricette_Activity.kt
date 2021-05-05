package com.example.cooking_app

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ricette_Adapter
import com.example.cooking_app.Classi.Ricetta
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_ricette_activity.*
import java.util.*
import kotlin.collections.ArrayList


/*
Main Activity con lista di ricette
 */

class List_Ricette_Activity : AppCompatActivity(){

    private val TAG = "List_Ricette_Activity"

    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference().child("ricette")
    private var mRicettaChildListener: ChildEventListener = getRicetteChildEventListener() //recupera il listener con le azioni da svolgere
    private var mRicetteValueListener: ValueEventListener = getDataToFireBase() //visulaizza i dati
    private var img: MutableList<Ricetta> = ArrayList()
    private val mAdapter = Lista_Ricette_Adapter(img as ArrayList<Ricetta>)


    //array di ricette
   /* private  val img = arrayListOf(
            R.drawable.img_1, R.drawable.img_2, R.drawable.img_3,
            R.drawable.img_4, R.drawable.img_5, R.drawable.img_6)*/

    lateinit var toggle: ActionBarDrawerToggle

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)



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

        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = mAdapter

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
        val searchItem = menu?.findItem(R.id.search_icon)
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

    //selezione del funzione della MenuBar laterale
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() { super.onStart()
        DBricette!!.addValueEventListener(mRicetteValueListener)         //aggiungiamo il listener degli eventi  per la lettura dei dati sul riferimento al DB
        //DBricette!!.addChildEventListener(mRicettaChildListener)         //aggiungiamo il listener degli eventi per i figli sul riferimento al DB
    }

    override fun onStop() {
        super.onStop()
        img.clear()  //cancello la lista delle ricette per non aggiungerle piu volte nel list_ricette = RecyclerView
        //DBricette!!.removeEventListener(mRicettaChildListener)
    }

    //lettura dei dati da Firebase
    private fun getDataToFireBase(): ValueEventListener{ //prima lettura dei dati dal Database o anche modifica dei Dati
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (ds in dataSnapshot.children) {
                    val ricetta: Ricetta? = ds.getValue(Ricetta::class.java)
                    Log.d(TAG, "$ricetta")
                    img.add(ricetta!!)
                }
                mAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Ricetta failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        return postListener
    }

    //funzione che crea il listener per le varie azioni effettuate sul DB e lo restituisce
    private fun getRicetteChildEventListener(): ChildEventListener {

        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                mAdapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        return childEventListener
    }
}

