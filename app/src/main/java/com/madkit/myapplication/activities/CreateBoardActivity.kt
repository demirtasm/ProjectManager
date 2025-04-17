package com.madkit.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.madkit.myapplication.R
import com.madkit.myapplication.databinding.ActivityCreateBoardBinding

class CreateBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBoardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarCreateBoardActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_board_title)
        }
        binding.toolbarCreateBoardActivity.setNavigationOnClickListener { onBackPressed() }
    }
}