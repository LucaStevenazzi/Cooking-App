package com.example.cooking_app.Classi

//classe che salva le immagini sullo storage

data class Upload(val nome : String, val ImageUrl : String) {

    constructor(): this("", "")
}