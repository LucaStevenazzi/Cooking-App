package com.example.cooking_app

import android.app.SearchManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.view_ricetta_activity.*


/*
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        Log.v("View_Ricetta_Activity", "Start onCreate")

        val img = intent.getIntExtra("immagine", 0)
        val imageResource = img_ricetta.setImageResource(img)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_or_delete,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.image_edit -> Toast.makeText(this, "Edit Selected", Toast.LENGTH_SHORT).show()
            R.id.image_delete -> Toast.makeText(this, "Delete Selected", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
}