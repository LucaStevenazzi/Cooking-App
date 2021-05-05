package com.example.cooking_app.Classi

import android.graphics.drawable.Drawable

/*
Classe Ricette
 */
data class Ricetta(val immagine : Int, val nome: String, val diff: String, val tempo: String, val tipologia: String, val portata: String, val persone: Int,
                   val listaIngredienti: ArrayList<Ingredienti>, val note: ArrayList<String>){

    constructor(): this(0, "", "", "", "", "", 0, ArrayList(), ArrayList())
}

