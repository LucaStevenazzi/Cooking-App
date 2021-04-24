package com.example.cooking_app

import com.example.cooking_app.Classi.Ingredienti
import java.sql.Time

/*
Classe Ricette
 */
data class Recipe(val nome : String, val diff : String, val tempo : String, val tipologia : String, val portata : String, val persone : Int,
                  val listaIngredienti: ArrayList<Ingredienti>, val note : ArrayList<String>){


}

