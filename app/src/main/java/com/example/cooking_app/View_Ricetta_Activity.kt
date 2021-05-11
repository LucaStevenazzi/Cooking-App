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
import kotlinx.android.synthetic.main.view_ricetta_activity.*


/*
Activity com.example.cooking_app.di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    private val TAG = "View_Ricetta_Activity"

    var ricetta : Ricetta = Ricetta()
    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference()

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        ricetta = getRicettaExtra()
        title = ricetta.nome // settagio del titolo della Activity
        setDati(ricetta)

    }

    private fun setDati(ricetta: Ricetta) {
        img_ricetta.setImageResource(ricetta.immagine)
        name_ricetta.text=ricetta.nome
    }

    private fun getRicettaExtra(): Ricetta { //ottenere la ricetta dall'intent com.example.cooking_app.di creazione

        var ricetta : Ricetta = Ricetta()
        ricetta.immagine = intent.getIntExtra("Immagine", 0)
        ricetta.nome = intent.getStringExtra("Nome").toString()
        ricetta.diff = intent.getStringExtra("Difficoltà").toString()
        ricetta.tempo = intent.getStringExtra("Tempo").toString()
        ricetta.tipologia = intent.getStringExtra("Tipologia").toString()
        ricetta.portata = intent.getStringExtra("Portata").toString()
        ricetta.persone = intent.getIntExtra("Persone", 0)
        ricetta.listaIngredienti = intent.getStringArrayListExtra("ListaIngredienti") as ArrayList<Ingredienti>
        ricetta.note = intent.getStringArrayListExtra("Note") as ArrayList<String>
        return ricetta
    }

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

    private fun editRicetta() {
        var intent = Intent(this, AddNewRecipeActivity::class.java)
        intent.putExtras(getIntent())
        startActivity(intent)
    }

    private fun deleteRicettaToList() { //elimino la ricetta che è stata aperta
        val ref = FirebaseDatabase.getInstance().reference
        val applesQuery: Query = ref.child("ricette").child(ricetta.nome)

        applesQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) { // in caso com.example.cooking_app.di errore
                Log.e("View_Ricetta_Activity", "onCancelled", databaseError.toException())
            }
        })
        //chiudo l'activiity
        finish()
    }
}