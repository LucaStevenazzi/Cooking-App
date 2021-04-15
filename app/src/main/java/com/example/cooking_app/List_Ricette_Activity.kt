package com.example.cooking_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.R
import kotlinx.android.synthetic.main.list_ricette_activity.*

class List_Ricette_Activity : AppCompatActivity() , onClickListener{

    private  val img = arrayOf(
            R.drawable.img_1,R.drawable.img_2,R.drawable.img_3,
            R.drawable.img_4,R.drawable.img_5,R.drawable.img_6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_ricette_activity)

        lista_ricette.layoutManager = LinearLayoutManager(this)
        lista_ricette.adapter = CustomAdapter(img,this)

    }

    override fun onClickListenerItem(position: Int) {
        val intent = Intent(this, View_Ricetta_Activity::class.java)
        intent.putExtra("immagine" , img[position])
        startActivity(intent)
    }

}
