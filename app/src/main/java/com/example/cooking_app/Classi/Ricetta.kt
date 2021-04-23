package com.example.cooking_app

import com.example.cooking_app.Classi.Ingredienti
import java.sql.Time

/*
Classe Ricette
 */
data class Recipe(val nome : String, val diff : Difficolta, val tempo : Time, val portata : Portata, val persone : Int,
                  val listaIngredienti: ArrayList<Ingredienti>, val note : ArrayList<String>){

    enum class Difficolta {
        BASSA, MEDIA, ALTA
    }

    enum class Portata {
        Antipasto, Primo, Secondo, Contorno, Dolce
    }
}

