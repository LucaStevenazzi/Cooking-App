package com.example.cooking_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Adapter.Lista_Ricette_Locali_Adapter
import com.example.cooking_app.Classi.*
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.activity_lista_ricette_locali.*
import kotlinx.android.synthetic.main.view_ricetta_activity.*
import kotlinx.android.synthetic.main.view_ricetta_activity.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/*
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var flag_first_Update: Boolean = true
    private val TAG = "View_Ricetta_Activity"
    private var lista_ingredienti = ArrayList<Ingredienti>()
    private var lista_ingredienti_copia = ArrayList<Ingredienti>()
    private var mAdapter: Lista_Ingredienti_Adapter = Lista_Ingredienti_Adapter(lista_ingredienti)
    private var ricetta : Ricetta = Ricetta()
    private val ref = FirebaseDatabase.getInstance().reference
    private val db : DataBaseHelper = DataBaseHelper(this)


    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        setComponent()

    }

    //settaggio dei componenti nell'activity
    private fun setComponent() { //settiamo la RecyclerView per la lista degli ingredienti nella View_Ricetta
        setSpinner()
        ricetta_ingredienti.layoutManager = LinearLayoutManager(this)
        ricetta_ingredienti.adapter = mAdapter //pasaggio del context per capire che activity chiama l'adapter
        getRicettaExtra() // prende la ricetta dagli extra dell'intent
        updateRicetta()
        setDati() // setta i dati della ricetta sull'layout dell'activity
    }

    private fun setSpinner() {

        //contenitore dei valori della DropDown List per il numero di persone
        ArrayAdapter.createFromResource(this, R.array.array_num_persone, android.R.layout.simple_spinner_item).also {
            adapter ->
            // Specifica il layout da usare quando la lista appare
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Applicazione dell'adapter allo spinner
            spinner_num_persone.adapter = adapter

            spinner_num_persone.onItemSelectedListener = this
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {

            lista_ingredienti.clear()
            lista_ingredienti_copia = ricetta.listaIngredienti
            val numeroPersone = parent.getItemAtPosition(position).toString().toInt()

            for (i in lista_ingredienti_copia.indices){
                val ing = Ingredienti(lista_ingredienti_copia[i].Name, lista_ingredienti_copia[i].quantit, lista_ingredienti_copia[i].misura)
                lista_ingredienti.add(ing)
                lista_ingredienti[i].quantit = (lista_ingredienti_copia[i].quantit.toDouble() / ricetta.persone * numeroPersone).toString()
            }

            mAdapter.notifyDataSetChanged()

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun getRicettaExtra(){ //ottenere la ricetta dall'intent di creazione

        val byteArray = intent.getByteArrayExtra("Bitmap")
        if (byteArray != null){
            ricetta.bit = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
        ricetta.immagine = intent.getStringExtra("Immagine").toString()
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficoltà").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        getIngredientiExtra()
        ricetta.note = intent.getStringExtra("Note").toString()
    }
    private fun getIngredientiExtra() {
        val count = intent.getIntExtra("Count" , 0)
        if(count == 0) return
        for(i in 0 until count){
            val ingnome = intent.getStringExtra("Ingrediente $i nome").toString()
            val ingquanti = intent.getStringExtra("Ingrediente $i quantità").toString()
            val ingmisura = intent.getStringExtra("Ingrediente $i misura").toString()
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)
            ricetta.listaIngredienti.add(ing)
        }
    }
    private fun setDati() {
        title = ricetta.nome // settagio del titolo della Activity
        if(ricetta.bit == null)
            Picasso.with(this).load(ricetta.immagine).into(img_ricetta)
        else
            img_ricetta.setImageBitmap(ricetta.bit)
        ricetta_time.text = ricetta.tempo.plus(" minuti")
        ricetta_difficolta.text = ricetta.diff
        when (ricetta.persone) {
            1 -> spinner_num_persone.setSelection(0)
            2 -> spinner_num_persone.setSelection(1)
            3 -> spinner_num_persone.setSelection(2)
            4 -> spinner_num_persone.setSelection(3)
            5 -> spinner_num_persone.setSelection(4)
            6 -> spinner_num_persone.setSelection(5)
            7 -> spinner_num_persone.setSelection(6)
            8 -> spinner_num_persone.setSelection(7)
            9 -> spinner_num_persone.setSelection(8)
            10 -> spinner_num_persone.setSelection(9)
        }
        ricetta_portata.text = ricetta.portata
        lista_ingredienti.clear()
        lista_ingredienti.addAll(ricetta.listaIngredienti)
        mAdapter.notifyDataSetChanged()
        //ricetta_note.text = ricetta.note
    }

    //fare l'update della ricetta modificata
    override fun onRestart() {
        super.onRestart()
        Log.v(TAG , "onRestart")
        updateRicetta()
        setDati()
    }
    private fun updateRicetta() {
        if(intent.getStringExtra("Activity Name") != "Lista_Ricette_Adapter")
            ricetta = db.readData(ricetta.nome,ricetta.note) //restituisce la ricetta locale modificata
        else{
            //update database online
            val applesQuery = ref.child("ricette")
            applesQuery.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        if (ds.key == ricetta.nome){
                            ricetta = ds.getValue(Ricetta::class.java)!!
                            Toast.makeText(this@View_Ricetta_Activity , "Update ${ricetta.nome} succes ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) { // in caso di errore
                    Log.e("View_Ricetta_Activity", "onCancelled", databaseError.toException())
                }
            })
        }
    }
    private fun setNewExtra() {
        val intent = Intent(Intent(this, AddNewRecipeActivity::class.java))
        putRicettaExtra(intent)
        startActivity(intent)
    }

    //passaggio tramite intent della ricetta selezionata
    private fun putRicettaExtra(intent: Intent) {//inserisco nell'intent i valori della ricetta che è stata cliccata
        intent.putExtra("Immagine", ricetta.immagine)
        intent.putExtra("Nome", ricetta.nome)
        intent.putExtra("Difficoltà", ricetta.diff)
        intent.putExtra("Tempo", ricetta.tempo)
        intent.putExtra("Tipologia", ricetta.tipologia)
        intent.putExtra("Portata", ricetta.portata)
        intent.putExtra("Persone", ricetta.persone)
        putIngredintiExtra(intent)
        intent.putExtra("ListaIngredienti", ricetta.listaIngredienti)
        intent.putExtra("Note", ricetta.note)

    }
    private fun putIngredintiExtra(intent: Intent) {//salvataggio nell'intent dei dati degli ingredienti
        val count = ricetta.listaIngredienti.size
        if(count == 0) return
        for( i in ricetta.listaIngredienti.indices) {
            intent.putExtra("Ingrediente $i nome", ricetta.listaIngredienti[i].Name)
            intent.putExtra("Ingrediente $i quantità", ricetta.listaIngredienti[i].quantit)
            intent.putExtra("Ingrediente $i misura", ricetta.listaIngredienti[i].misura)
        }
        intent.putExtra("Count", count)
    }

    //creazione dell'option menu Edit and Delete delle ricette
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //inizializzaizione OptionsMenu
        menuInflater.inflate(R.menu.edit_or_delete, menu)
        if(intent.getStringExtra("Activity Name") == "Lista_Ricette_Adapter"){
            val delete = menu?.findItem(R.id.image_delete)
            val edit = menu?.findItem(R.id.image_edit)
            if(!db.controllaRicetta(ricetta.note,ricetta.nome))
                delete?.isVisible = false //settato a true solo se è presente nel DB locale
            edit?.isVisible = false
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Premuta pulsanti opzioni per la modifica o la cancellazione della ricetta
        when(item.itemId){
            R.id.image_edit -> {
                Toast.makeText(this, "Edit ${ricetta.nome}", Toast.LENGTH_SHORT).show()
                editRicetta()
            }
            R.id.image_delete -> {
                Toast.makeText(this, "Delete ${ricetta.nome}", Toast.LENGTH_SHORT).show()
                deleteRicettaFromList()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun editRicetta() { //far partire una l'activity per l'edit di una ricetta
        if(flag_first_Update){
            val intent = Intent(this, AddNewRecipeActivity::class.java)
            intent.putExtras(this.intent) // prendo l'intent gia precedentemente utilizzato per quest'activity
            flag_first_Update = false
            startActivity(intent)
        }else {//locale
            val intent = Intent(this, AddNewRecipeActivity::class.java)
            putRicettaExtra(intent)
            startActivity(intent)
            //val arrayVuoto =  ArrayList<Ricetta>()
            //val adp = Lista_Ricette_Locali_Adapter(arrayVuoto)
            //adp.putRicettaLocaleExtra(intent, ricetta)
        }

        //se ricetta è online allora update
        //setNewExtra()// settaggio dei nuovi dati nell'intent per un'altra probabile modifica

    }
    private fun deleteRicettaFromList() { //elimino la ricetta che è stata aperta

        val ricetta_da_eliminare =  db.readData(ricetta.nome,ricetta.note)//salvo la ricetta per il campo imagine
        if (ricetta.bit != null){//locale
            db.eliminaRicetta(ricetta.note, ricetta.nome)
            //bisogna eliminarla anche online
        }
        //online
        val applesQuery: Query = ref.child("ricette").child(ricetta.nome + ricetta_da_eliminare.immagine )

        applesQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { // in caso di errore
                Log.e("View_Ricetta_Activity", "onCancelled", databaseError.toException())
            }
        })

        finish()//chiudo l'activiity
    }
    //Conversione
    private val mapGrammi: HashMap<Int,String> = HashMap()
    private val mapCucchiaini: HashMap<Int,String> = HashMap()
    private val mapMl: HashMap<Int,String> = HashMap()
    private val mapBicchieri: HashMap<Int,String> = HashMap()
    fun setGrammi(view: View) {
        if(view.isSelected){
            view.isSelected = false
            resetGrammi()
            resetCucchiani()
        }
        else{
            view.isSelected = true
            cucchiai.isSelected = false
            resetGrammi()
            for(i in lista_ingredienti.indices){//operazioni
                val ing = lista_ingredienti[i]
                if(ing.misura == "cucchiaini"){
                    mapCucchiaini[i] = ing.quantit
                    var quant = ing.quantit.toDouble()
                    quant*=5
                    ing.quantit = quant.toString()
                    ing.misura = "g"
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
    fun setCucchiaini(view: View) {
        if (view.isSelected) {
            view.isSelected = false
            resetGrammi()
            resetCucchiani()
        }
        else {
            view.isSelected = true
            grammi.isSelected = false
            resetCucchiani()
            for (i in lista_ingredienti.indices) {//operazioni
                val ing = lista_ingredienti[i]
                if (ing.misura == "g") {
                    mapGrammi[i] = ing.quantit
                    var quant = ing.quantit.toDouble()
                    quant /= 5
                    ing.quantit = quant.toString()
                    ing.misura = "cucchiaini"
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
    fun setMl(view: View) {
        if(view.isSelected){
            view.isSelected = false
            resetMl()
            resetBicchieri()
        }
        else{
            view.isSelected = true
            bicchieri.isSelected = false
            resetMl()
            for(i in lista_ingredienti.indices){//operazioni
                val ing = lista_ingredienti[i]
                if(ing.misura == "bicchieri"){
                    mapBicchieri[i] = ing.quantit
                    var quant:Double = ing.quantit.toDouble()
                    quant*=200
                    ing.quantit = quant.toString()
                    ing.misura = "ml"
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
    fun setBicchieri(view: View) {
        if(view.isSelected){
            view.isSelected = false
            resetMl()
            resetBicchieri()
        }
        else{
            view.isSelected = true
            ml.isSelected = false
            resetBicchieri()
            for(i in lista_ingredienti.indices){//operazioni
                val ing = lista_ingredienti[i]
                if(ing.misura == "ml"){
                    mapMl[i] = ing.quantit
                    var quant = ing.quantit.toDouble()
                    quant/=200
                    ing.quantit = quant.toString()
                    ing.misura = "bicchieri"
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
    private fun resetGrammi() {
        for (valore in mapGrammi) {//reset ingredienti di tipo grammi
            lista_ingredienti[valore.key].quantit = valore.value
            lista_ingredienti[valore.key].misura = "g"
        }
    }
    private fun resetCucchiani() {
        for(valore in mapCucchiaini){//reset ingredienti di tipo cucchiaini
            lista_ingredienti[valore.key].quantit = valore.value
            lista_ingredienti[valore.key].misura = "cucchiaini"
        }
    }
    private fun resetMl() {
        for(valore in mapMl){//reset ingredienti di tipo cl
            lista_ingredienti[valore.key].quantit = valore.value
            lista_ingredienti[valore.key].misura = "ml"
        }
    }
    private fun resetBicchieri() {
        for(valore in mapBicchieri){//reset ingredienti di tipo Bicchieri
            lista_ingredienti[valore.key].quantit = valore.value
            lista_ingredienti[valore.key].misura = "bicchieri"
        }
    }

}