package com.example.cooking_app

/*
classe degli ingredienti
 */

data class Ingredienti (val Name : String ,  val quantit : Int , val tipo_quantit : misura?){




    enum class misura {
        ml, g , cucchiani

    }
}