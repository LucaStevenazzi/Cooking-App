package com.example.cooking_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cooking_app.Classi.Ingredienti

@Entity(tableName = "Tabella ricette")
data class RicettaTab(
    var immagine: Int,
    var nome: String,
    var diff: String,
    var tempo: String,
    var tipologia: String,
    var portata: String,
    var persone: Int,
    var listaIngredienti: ArrayList<Ingredienti>,
    var note: ArrayList<String>
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}