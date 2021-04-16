package com.example.cooking_app

import java.sql.Time

/*
Classe Ricette
 */
data class Recipe(val Name : String, val List_Ingredients: ArrayList<Ingredienti>, val Prepare_Time : Time , val Cooking_Time : Time, val Level : Difficolta, val doses : Int ){

    enum class Difficolta {
        BASSA , MEDIA , ALTA
    }
}

