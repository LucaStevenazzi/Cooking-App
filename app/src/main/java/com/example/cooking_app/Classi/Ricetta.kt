package com.example.cooking_app.Classi

import android.graphics.Bitmap

/*
Classe Ricette
 */
data class Ricetta(var immagine: String, var nome: String, var diff: String, var tempo: String, var tipologia: String, var portata: String, var persone: Int,
                   var listaIngredienti: ArrayList<Ingredienti>, var note: String){

    var bit : Bitmap? = null

    constructor(bitmap : Bitmap, nome: String, diff: String,tempo: String, tipologia: String, portata: String, persone: Int,
                listaIngredienti: ArrayList<Ingredienti>, note: String) : this(immagine = "", nome, diff, tempo, tipologia, portata, persone, listaIngredienti, note){
                  bit = bitmap
                }

    constructor(): this("","Ricetta Default", "", "", "", "", 0, ArrayList<Ingredienti>(), "")
}

