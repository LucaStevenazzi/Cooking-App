package com.example.cooking_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.Adapter.Lista_Ricette_Adapter
import com.example.cooking_app.Classi.Ricetta
import com.example.cooking_app.Fragment.Filtro_ricerca
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.filtro_ricerca_fragment.*
import kotlinx.android.synthetic.main.list_ricette_activity.*

/*
Spesso capita che le dosi di una ricetta siano per un numero non adatto alle proprie esigenze, obbligando a dover fare le relative proporzioni degli ingredienti.
Se poi si vuole riproporre la stessa ricetta per un numero diverso di persone, tale processo deve essere ripetuto. Da qui l’idea di quanto segue.

Lo scopo principale dell’applicazione è quello di, una volta memorizzata una ricetta, calcolare le quantità dei vari ingredienti proporzionalmente al numero di commensali (scelto dall’utente).

Altre funzionalità offerte dell’applicazione sono:

Generare una lista della spesa per poter essere inviata a un contatto tramite diversi canali (email, whatsapp, …)
Gestione del proprio ricettario (permanenza ricette)
Possibilità di modificare ricette, aggiungendo, rimuovendo o cambiando le dosi di ingredienti indipendentemente dalle proporzioni
Possibilità di aggiungere delle note alle ricette (trucchi, collegamenti a tutorial, osservazioni, …)
Possibilità di ricercare di una ricetta: sia attraverso filtri tipo portata, tipologia, tempo, difficoltà, … sia per ingredienti (cucina con ciò che hai)
Conversione tra diversi grassi animali e vegetali (burro-olio, burro-ricotta, …)
Conversione cucchiaio - grammi e bicchiere - cl

App Rivolta sia a professionisti del settore che a cuochi per passione.
 */

class List_Ricette_Activity : AppCompatActivity(){

    private val TAG = "List_Ricette_Activity"
    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference().child("ricette") //radice dell'albero per la View delle ricette
    private lateinit var mRicetteValueListener: ValueEventListener
    private var img: ArrayList<Ricetta> = ArrayList()
    private lateinit var mAdapter: Lista_Ricette_Adapter
    private lateinit var toggle: ActionBarDrawerToggle
    private var Frag_search = Filtro_ricerca()

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)

    }

    //OnClick: apertura nuova activity per l'aggiunta di una ricetta
    fun newRecipe(v: View) {
        val it = Intent(this, AddNewRecipeActivity::class.java)
        startActivity(it)
    }

    fun apriRicettarioLocale(v : View){
        val it = Intent(this, Lista_Ricette_Locali_Activity::class.java)
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
                nascondi_filtro_ricetta()
                return true
            }
        })
        return true
    }

    private var isOpen = true
    private var createFragmente = true

    override fun onOptionsItemSelected(item: MenuItem): Boolean {//selezione del funzione dell'OptionMenu
        return when(item.itemId){
            R.id.search_filter -> {
                start_filtro_ricette()
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

    //filtro_ricerca
    private fun start_filtro_ricette() {
        if(isOpen) {
            lista_ricette.visibility = RecyclerView.GONE
            salvaRicetta.visibility = FloatingActionButton.GONE
            apriRicettarioLocale.visibility = FloatingActionButton.GONE
            hideKeyboard()
            if(createFragmente){
                supportFragmentManager.beginTransaction().add(R.id.Frag_filter_search, Frag_search).addToBackStack(null).commit()
                Frag_filter_search.visibility = FrameLayout.VISIBLE
                createFragmente = false
            }else{
                Frag_filter_search.visibility = FrameLayout.VISIBLE
            }
            isOpen = false
        }
        else{
            nascondi_filtro_ricetta()
        }
    }
    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
    private fun nascondi_filtro_ricetta() {
        isOpen = true
        lista_ricette.visibility = RecyclerView.VISIBLE
        salvaRicetta.visibility = FloatingActionButton.VISIBLE
        apriRicettarioLocale.visibility = FloatingActionButton.VISIBLE
        Frag_filter_search.visibility = FrameLayout.GONE
        mAdapter.filter(getTextButtton()).filter("")
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
                lista_ricette.visibility = RecyclerView.VISIBLE
                salvaRicetta.visibility = FloatingActionButton.VISIBLE
                apriRicettarioLocale.visibility = FloatingActionButton.VISIBLE
                Frag_filter_search.visibility = FrameLayout.GONE
                //controlla nell'array di ricette
                if(createFragmente)
                    mAdapter.filter.filter(newText)
                else
                    mAdapter.filter(getTextButtton()).filter(newText)
                return true
            }
        })
    }

    private fun getTextButtton(): ArrayList<String> {
        var list_filter = ArrayList<String>()
        //salvataggio button Difficoltà
        if(bt_fDifficolta != null)
            list_filter.add(bt_fDifficolta!!.text.toString())
        else
            list_filter.add("null")
        //salvataggio button Tempo
        if(bt_fTempo != null) {
            var string = ""
            when(bt_fTempo!!.text){
                "1-15 min" -> string = "15"
                "15-30 min" -> string = "30"
                "+30 min" -> string = "60"
            }
            list_filter.add(string)
        }
        else
            list_filter.add("null")
        //salvataggio text Tipologia
        if(et_tipologia.text.isNotEmpty())
            list_filter.add(et_tipologia.text.toString())
        else
            list_filter.add("null")
        //salvataggio button Portata
        if(bt_fPortata != null)
            list_filter.add(bt_fPortata!!.text.toString())
        else
            list_filter.add("null")
        //salvataggio text Ingrediente
        if(et_ingrediente.text.isNotEmpty())
            list_filter.add(et_ingrediente.text.toString())
        else
            list_filter.add("null")
        return list_filter
    }

    //propriety per il filtro della ricerca
    private var bt_fDifficolta : Button? = null
    private var bt_fTempo : Button? = null
    private var bt_fPortata : Button? = null

    fun fDifficolta(view: View) {
        if(bt_fDifficolta != null ){
            bt_fDifficolta?.isSelected = false
        }
        if(view == bt_fDifficolta){
            bt_fDifficolta = null
            return
        }else{
            view.isSelected = true
            bt_fDifficolta = view as Button
        }
    }

    fun fTempo(view: View) {
        if(bt_fTempo != null ){
            bt_fTempo?.isSelected = false
        }
        if(view == bt_fTempo){
            bt_fTempo = null
            return
        }else{
            view.isSelected = true
            bt_fTempo = view as Button
        }
    }

    fun fTipologia(view: View){

    }

    fun fPortata(view: View){
        if(bt_fPortata != null ){
            bt_fPortata?.isSelected = false
        }
        if(view == bt_fPortata){
            bt_fPortata = null
            return
        }else{
            view.isSelected = true
            bt_fPortata = view as Button
        }
    }

    //lettura dei dati da Firebase e  inizializzazione della lista delle ricette
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
    private fun initRecyclerView() {
        mAdapter = Lista_Ricette_Adapter(img, this)
        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = mAdapter
    }
}

