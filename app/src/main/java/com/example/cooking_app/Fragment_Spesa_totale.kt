package com.example.cooking_app

import android.R.attr.defaultValue
import android.R.attr.key
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cooking_app.Classi.Ingredienti
import kotlinx.android.synthetic.main.fragment_spesa_totale_list.view.*


/**
 * A fragment representing a list of Items.
 */
class Fragment_Spesa_totale : Fragment() {

    private var lista_ingredienti_totali = ArrayList<Ingredienti>()
    private var lista_ingredienti = ArrayList<Ingredienti>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*CON INTENT
        val intent = Intent(context,AddSpesa::class.java)
        lista_ingredienti.addAll(activity?.intent?.getSerializableExtra("ingredienti da aggiungere") as ArrayList<Ingredienti>)
         */
            lista_ingredienti =
                arguments?.getSerializable("lista parziale") as ArrayList<Ingredienti>

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_spesa_totale_list, container, false)
        lista_ingredienti_totali.addAll(lista_ingredienti)
        // Set the adapter
        view.list_TOT.adapter = MyItemRecyclerViewAdapter2(lista_ingredienti_totali)
        view.list_TOT.layoutManager = LinearLayoutManager(view.context)
        return view
    }
}