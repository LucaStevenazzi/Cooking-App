package com.example.cooking_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Adapter.Lista_Ingredienti_Adapter
import com.example.cooking_app.Classi.Ingredienti
import kotlinx.android.synthetic.main.lista_spesa.*
import java.lang.StringBuilder


class Lista_Spesa : AppCompatActivity(){

    private var spesa = ArrayList<Ingredienti>()
    private var stringa = StringBuilder()
    private var ingrediente = Ingredienti()

    //creazione activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lista_spesa)
        setComponent()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.share ->{
                share(spesa,spesa.size)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun share(spesa : ArrayList<Ingredienti>, count : Int){
        stringa.clear()
        for(i in 0 until count){
            stringa.append(spesa[i])
            stringa.append("\n")
        }
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, stringa.toString())
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "lista spesa")
        startActivity(Intent.createChooser(shareIntent, "condividi via"))
     }

    private fun setComponent() {
        listaSpesa.layoutManager = LinearLayoutManager(this)
        listaSpesa.adapter = Lista_Ingredienti_Adapter(spesa)
        getIngredientiExtra()
    }

    private fun getIngredientiExtra() {
        val count = intent.getIntExtra("Count" , 0)
        if(count == 0) return
        for(i in 0 until count){
            val ingnome = intent.getStringExtra("Ingrediente $i nome").toString()
            val ingquanti = intent.getStringExtra("Ingrediente $i quantità").toString()
            val ingmisura = intent.getStringExtra("Ingrediente $i misura").toString()
            val ing = Ingredienti(ingnome, ingquanti, ingmisura)
            spesa.add(ing)
        }
    }
}