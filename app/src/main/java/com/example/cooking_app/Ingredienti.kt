package com.example.cooking_app

/*
classe degli ingredienti
 */

data class Ingredienti (val Name : String ,  val quantit : Int? , val tipo_quantit : misura?){

    //capire bene come gestire questi oggetti prendendoli dallo stesso edittext, opuure cambiare la modalità di acquisizione


    enum class misura {
        ml, gr , cucchiani, pz

    }
}