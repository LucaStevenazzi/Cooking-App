package com.example.cooking_app

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
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    var ricetta : Ricetta = Ricetta()
    private var DBricette : DatabaseReference? = FirebaseDatabase.getInstance().getReference()

    //inizializzazione Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        ricetta = getRicettaExtra()
        img_ricetta.setImageResource(ricetta.immagine)

    }

    private fun getRicettaExtra(): Ricetta { //ottenere la ricetta dall'intent di creazione

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

    //inizializzaizione OptionsMenu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_or_delete, menu)
        return true
    }

    //test premuta pulsanti
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.image_edit -> Toast.makeText(this, "Edit Selected", Toast.LENGTH_SHORT).show()
            R.id.image_delete -> {
                Toast.makeText(this, "Delete Selected", Toast.LENGTH_SHORT).show()
                deleteRicettaToList()
            }
        }
        return super.onOptionsItemSelected(item)
    }

   private fun deleteRicettaToList() { //elimino la ricetta che è stata aperta
        val ref = FirebaseDatabase.getInstance().reference
        val applesQuery: Query = ref.child("ricette").child(ricetta.nome)

        applesQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("View_Ricetta_Activity", "onCancelled", databaseError.toException())
            }
        })
        //chiudo l'activiity
        finish()
    }
}