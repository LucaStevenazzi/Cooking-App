package com.example.cooking_app

data class Ingredienti (val Name : String ,  val quantità : Int , val tipo_quantità : misura?){

}

enum class misura {
    ml, g , cucchiani
}

