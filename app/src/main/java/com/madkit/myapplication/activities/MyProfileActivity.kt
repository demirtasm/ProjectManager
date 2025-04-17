package com.madkit.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.madkit.myapplication.R
import com.madkit.myapplication.databinding.ActivityMyProfileBinding
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.User

class MyProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        FirestoreClass().loadUserData(this)
    }

    fun setUserDataInUI(user: User) {

        Glide.with(this@MyProfileActivity).load(user.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(binding.ivUserImage)
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if(user.mobile != 0L){
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }
}