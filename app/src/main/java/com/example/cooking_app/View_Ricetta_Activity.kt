package com.example.cooking_app

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.Classi.Ricetta
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_new_recipe.*
import kotlinx.android.synthetic.main.view_ricetta_activity.*
import kotlinx.android.synthetic.main.view_ricetta_activity.view.*


/*
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    private var flag_first_Update: Boolean = true
    private val TAG = "View_Ricetta_Activity"

    private var ricetta : Ricetta = Ricetta()
    private var lista_ingredienti = ArrayList<Ingredienti>()
    private val ref = FirebaseDatabase.getInstance().reference


    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        Log.v(TAG , "onCreate")
        setComponent()

    }

    //settaggio dei componenti nell'activity
    private fun setComponent() { //settiamo la RecyclerView per la lista degli ingredienti nella View_Ricetta
        ricetta_ingredienti.layoutManager = LinearLayoutManager(this)
        ricetta_ingredienti.adapter = Lista_Ingredienti_Adapter(lista_ingredienti) //pasaggio del context per capire che activity chiama l'adapter
        getRicettaExtra() // prende la ricetta dagli extra dell'intent
        updateRicetta()
        setDati() // setta i dati della ricetta sull'layout dell'activity
    }
    private fun getRicettaExtra(){ //ottenere la ricetta dall'intent di creazione

        val byteArray = intent.getByteArrayExtra("Bitmap")
        if (byteArray != null){
            ricetta.bit = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
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
            lista_ingredienti.add(ing)
        }
    }
    private fun setDati() {
        title = ricetta.nome // settagio del titolo della Activity
        try {
            Picasso.with(this).load(ricetta.immagine).into(img_ricetta)
        } catch (e : IllegalArgumentException){
            img_ricetta.setImageBitmap(ricetta.bit)
        }
        ricetta_time.text = ricetta.tempo
        ricetta_difficolta.text = ricetta.diff
        ricetta_persone.text = ricetta.persone.toString()
        ricetta_portata.text = ricetta.portata
        ricetta.listaIngredienti = lista_ingredienti
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
                deleteRicettaToList()
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
        }else{
            setNewExtra()// settaggio dei nuovi dati nell'intent per un'altra probabile modifica
        }

    }
    private fun deleteRicettaToList() { //elimino la ricetta che è stata aperta
        val applesQuery: Query = ref.child("ricette").child(ricetta.nome)

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
}