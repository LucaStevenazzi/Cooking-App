package com.example.cooking_app.Classi

import java.io.Serializable

/*
classe degli ingredienti
 */

data class Ingredienti(var Name: String, var quantit: String, var misura: String?)
    : Serializable{ //aggiunta serializable perchè dava un errore in Runtime quando trasferivo i dati dalla lista delle ricette alla visualizazione di una ricetta singola

    constructor(): this ("", "" , "")

}