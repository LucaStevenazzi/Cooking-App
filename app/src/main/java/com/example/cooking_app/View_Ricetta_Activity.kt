package com.example.cooking_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.example.cooking_app.R
import kotlinx.android.synthetic.main.view_ricetta_activity.*

/*
Activity di visualizzazione scelta della ricetta dall'elenco (Lista)
 */
class View_Ricetta_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_ricetta_activity)

        Log.v("View_Ricetta_Activity", "Start onCreate")

        val img = intent.getIntExtra("immagine" , 0)
        img_ricetta.setImageResource(img)

    }
}