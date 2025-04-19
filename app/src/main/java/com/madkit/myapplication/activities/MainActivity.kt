package com.madkit.myapplication.activities

import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.madkit.myapplication.R
import com.madkit.myapplication.adapters.BoardItemAdapter
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.Board
import com.madkit.myapplication.models.User
import com.madkit.myapplication.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var fabButton: FloatingActionButton
    private lateinit var tvUserName: TextView
    private lateinit var navUserImage: CircleImageView
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNoBoards: TextView
    private lateinit var boardAdapter: BoardItemAdapter

    companion object {
        const val MY_PROFILE_REQUEST_CODE = 11
        const val CREATE_BOARD_REQUEST_CODE = 12
    }

    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar_main_activity)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        fabButton = findViewById(R.id.fab_create_board)
        rvBoard = findViewById(R.id.rv_board_list)
        tvNoBoards = findViewById(R.id.tv_no_boards_available)
        val headerView = navView.getHeaderView(0)
        navUserImage = headerView.findViewById(R.id.nav_user_image)
        tvUserName = headerView.findViewById(R.id.tv_username)
        setUpActionBar()

        fabButton.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
        navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)
    }

    fun populateBoardsListToUI(boardList: ArrayList<Board>) {
        hideProgressDialog()
        if (boardList.size > 0) {
            rvBoard.visibility = View.VISIBLE
            tvNoBoards.visibility = View.GONE
            rvBoard.layoutManager = LinearLayoutManager(this)
            rvBoard.setHasFixedSize(true)
            boardAdapter = BoardItemAdapter(this, boardList)
            rvBoard.adapter = boardAdapter

            boardAdapter.setOnClickListener(object : BoardItemAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }

            })

        } else {
            tvNoBoards.visibility = View.VISIBLE
            rvBoard.visibility = View.GONE

        }
    }

    private fun setUpActionBar() {

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)

        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            FirestoreClass().loadUserData(this)
        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            FirestoreClass().getBoardsList(this)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean) {
        mUserName = user.name
        Glide.with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        tvUserName.text = user.name
        if (readBoardList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }
}