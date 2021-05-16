package com.example.cooking_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cooking_app.Classi.Ingredienti
import com.example.cooking_app.Classi.Ricetta
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_ricetta_activity.*


/*
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    private val TAG = "View_Ricetta_Activity"

    private var ricetta : Ricetta = Ricetta()
    private var ingredienti : Ingredienti = Ingredienti()
    private val ref = FirebaseDatabase.getInstance().reference
    private var flag_update =  false

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        Log.v(TAG , "onCreate")

        ricetta = getRicettaExtra() // prende la ricetta dagli extra dell'intent
        updateRicetta()
        setDati() // setta i dati della ricetta sull'layout dell'activity
    }
    private fun getRicettaExtra(): Ricetta { //ottenere la ricetta dall'intent di creazione

        var ricetta : Ricetta = Ricetta()
        ricetta.immagine = intent.getStringExtra("Immagine").toString()
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficoltà").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        getIngredientiExtra()
        ricetta.note = intent.getStringExtra("Note").toString()
        return ricetta
    }
    private fun getIngredientiExtra() {
        val count = intent.getIntExtra("Count" , 0)
        if(count == 0) return
        for(i in 0 until count){
            ingredienti.Name = intent.getStringExtra("Ingrediente $i nome").toString()
            ingredienti.quantit = intent.getStringExtra("Ingrediente $i quantità").toString()
            ingredienti.misura = intent.getStringExtra("Ingrediente $i misura").toString()
            ricetta.listaIngredienti.add(ingredienti)
        }
    }
    private fun setDati() {
        title = ricetta.nome // settagio del titolo della Activity
        Picasso.with(this).load(ricetta.immagine).into(img_ricetta)
        ricetta_time.text = ricetta.tempo
        ricetta_difficolta.text = ricetta.diff
        ricetta_persone.text = ricetta.persone.toString()
        ricetta_portata.text = ricetta.portata
        ricetta_ingredienti.text = ricetta.listaIngredienti.toString()
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
        val intent = Intent(this, AddNewRecipeActivity::class.java)
        intent.putExtras(this.intent) // prendo l'intent gia precedentemente utilizzato per quest'activity
        startActivity(intent)
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