package com.example.cooking_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.cooking_app.R
import kotlinx.android.synthetic.main.view_ricetta_activity.view.*

class CustomAdapter(val img: Array<Int> , private val onClickListener: onClickListener): RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {


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
    override fun getItemCount(): Int {
        return img.size
    }

}