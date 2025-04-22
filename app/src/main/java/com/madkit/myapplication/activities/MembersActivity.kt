package com.madkit.myapplication.activities

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
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
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMemberListDetails(
                this@MembersActivity,
                mBoardDetails.assignedTo
            )
        }
    }

    fun setUpMemberList(list: ArrayList<User>) {
        mAssignedMembersList = list
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(
            this
        )
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener {
            val email = dialog.findViewById<AppCompatEditText>(R.id.et_email_search_member).text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            } else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter email address",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun memberDetails(user: User) {
        mBoardDetails.assignedTo.add(
            user.id
        )
        FirestoreClass().assignedMemberToBoard(this, mBoardDetails, user)
    }

    fun memberAssignedSuccess(user:User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setUpMemberList(mAssignedMembersList)
    }
}