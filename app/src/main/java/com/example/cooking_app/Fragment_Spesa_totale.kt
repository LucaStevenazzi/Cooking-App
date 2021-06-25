package com.example.cooking_app

import android.content.Intent.getIntent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cooking_app.Classi.Ingredienti
import kotlinx.android.synthetic.main.fragment__spesa_totale_list.view.*

/**
 * A fragment representing a list of Items.
 */
class Fragment_Spesa_totale : Fragment() {

    private var lista_ingredienti_totali = ArrayList<Ingredienti>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lista_ingredienti_totali = arguments?.getSerializable("lista ingredienti totali") as ArrayList<Ingredienti>
        lista_ingredienti_totali.addAll(arguments?.getSerializable("ingredienti da aggiungere") as ArrayList<Ingredienti>)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment__spesa_totale_list, container, false)
        // Set the adapter
        view.list_TOT.adapter = MyItemRecyclerViewAdapter(lista_ingredienti_totali)
        view.list_TOT.layoutManager = LinearLayoutManager(view.context)
        return view
    }
}