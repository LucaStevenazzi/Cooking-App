package com.example.cooking_app.Classi

/*
Classe Ricette
 */
data class Ricetta(var immagine : Int, var nome : String, var diff : String, var tempo : String, var tipologia : String, var portata : String, var persone : Int,
                   var listaIngredienti: ArrayList<Ingredienti>, var note : ArrayList<String>){

    constructor(): this(0,"Ricetta Default", "", "", "", "", 0, ArrayList(), ArrayList())
}

