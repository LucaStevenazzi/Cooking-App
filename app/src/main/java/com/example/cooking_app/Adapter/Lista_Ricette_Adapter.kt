package com.example.cooking_app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.R
import com.example.cooking_app.Listener.onClickListener

/*
classe adattatatrice che permette di gestire la Lista (RecyclerView)
 */
class Lista_Ricette_Adapter(val img: ArrayList<Int>, private val onClickListener: onClickListener): RecyclerView.Adapter<Lista_Ricette_Adapter.CustomViewHolder>() {


    class CustomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bindValue(image: Int){
            itemView.findViewById<ImageView>(R.id.img_ricetta).setImageResource(image)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ricetta_list,parent,false))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        //setting delle immagini e titoli
        holder.bindValue(img[position])

        holder.itemView.setOnClickListener{
            onClickListener.onClickListenerItem(position)
        }

    }

    //metoco che restituisce il numero di Item nella lista delle ricette
    override fun getItemCount(): Int {
        return img.size
    }

}