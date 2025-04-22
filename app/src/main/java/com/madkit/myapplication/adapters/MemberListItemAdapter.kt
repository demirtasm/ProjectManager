package com.madkit.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.madkit.myapplication.R
import com.madkit.myapplication.models.User
import de.hdodenhof.circleimageview.CircleImageView

class MemberListItemAdapter(private val context: Context, private var list: ArrayList<User>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        )
    }

    override fun getItemCount(): Int {
      return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder).into(holder.ivMemberImage)
            holder.tvMemberName.text = model.name
            holder.tvMemberEmail.text = model.email
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivMemberImage = view.findViewById<CircleImageView>(R.id.iv_member_image)
        var tvMemberName = view.findViewById<TextView>(R.id.tv_member_name)
        var tvMemberEmail = view.findViewById<TextView>(R.id.tv_member_email)
    }
}