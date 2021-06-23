package com.example.cooking_app.Fragment

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.cooking_app.R
import kotlinx.android.synthetic.main.filtro_ricerca_fragment.*


class Filtro_ricerca : Fragment(){

    private val TAG = "Filtro_ricerca"
    private var stateBT: BooleanArray? = BooleanArray(11)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var root = inflater.inflate(R.layout.filtro_ricerca_fragment, container, false)
        return root
    }
}