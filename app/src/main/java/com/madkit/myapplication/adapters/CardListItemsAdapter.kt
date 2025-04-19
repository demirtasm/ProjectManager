package com.madkit.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.madkit.myapplication.R
import com.madkit.myapplication.models.Card

open class CardListItemsAdapter(private val context: Context, private var list: ArrayList<Card>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            holder.tvCardName.text = model.name
        }
    }
    class MyViewHolder(view:View): RecyclerView.ViewHolder(view){
        var tvCardName = view.findViewById<TextView>(R.id.tv_card_name)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }
    interface  OnClickListener{
        fun onClick(position: Int, card: Card)
    }
}