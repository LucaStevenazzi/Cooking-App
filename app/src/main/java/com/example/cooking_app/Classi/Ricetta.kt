package com.example.cooking_app.Classi

import android.graphics.Bitmap

/*
Classe Ricette
 */
data class Ricetta(var bit : Bitmap?, var immagine: String, var nome: String, var diff: String, var tempo: String, var tipologia: String, var portata: String, var persone: Int,
                   var listaIngredienti: ArrayList<Ingredienti>, var note: String){

    constructor(): this(null,"","Ricetta Default", "", "", "", "", 0, ArrayList<Ingredienti>(), "")
}

