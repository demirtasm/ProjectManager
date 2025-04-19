package com.madkit.myapplication.activities

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.madkit.myapplication.R
import com.madkit.myapplication.adapters.TaskListItemAdapter
import com.madkit.myapplication.databinding.ActivityMyProfileBinding
import com.madkit.myapplication.databinding.ActivityTaskListBinding
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.Board
import com.madkit.myapplication.models.Task
import com.madkit.myapplication.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board) {
       // hideProgressDialog()
        mBoardDetails = board
        setUpActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemAdapter(this, board.taskList)
        binding.rvTaskList.adapter = adapter
    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName:String){
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTakList(this, mBoardDetails)
    }

    fun updateTaskList(position:Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTakList(this, mBoardDetails)
    }

    fun deleteTasList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTakList(this, mBoardDetails)
    }
}