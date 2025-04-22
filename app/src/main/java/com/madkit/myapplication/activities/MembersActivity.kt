package com.madkit.myapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.madkit.myapplication.R
import com.madkit.myapplication.adapters.MemberListItemAdapter
import com.madkit.myapplication.databinding.ActivityMembersBinding
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.Board
import com.madkit.myapplication.models.User
import com.madkit.myapplication.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetais: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetais = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMemberListDetails(
                this@MembersActivity,
                mBoardDetais.assignedTo
            )
        }
    }

    fun setUpMemberList(list: ArrayList<User>) {
        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemAdapter(this, list)
        binding.rvMembersList.adapter = adapter
    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
            binding.toolbarMembersActivity.setNavigationOnClickListener {
                onBackPressed()
            }
        }

    }
}