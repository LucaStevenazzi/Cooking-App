package com.example.cooking_app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Classi.Ingredienti
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.fragment_item_list.view.*

/**
 * A fragment representing a list of Items.
 */
class AddSpesa : Fragment(){

    private var lista_ingredienti = ArrayList<Ingredienti>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lista_ingredienti =
            arguments?.getSerializable("lista ingredienti") as ArrayList<Ingredienti>
        bottoneLista.setOnClickListener() {
            val intent = Intent(context,Fragment_Spesa_totale::class.java)
            intent.putExtra("ingredienti da aggiungere",lista_ingredienti)
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        // Set the adapter
        view.list.adapter = MyItemRecyclerViewAdapter(lista_ingredienti)
        view.list.layoutManager = LinearLayoutManager(view.context)
        return view
    }
}