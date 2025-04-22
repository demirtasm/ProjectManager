package com.madkit.myapplication.firebase

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.madkit.myapplication.activities.CreateBoardActivity
import com.madkit.myapplication.activities.MainActivity
import com.madkit.myapplication.activities.MembersActivity
import com.madkit.myapplication.activities.MyProfileActivity
import com.madkit.myapplication.activities.SignInActivity
import com.madkit.myapplication.activities.SignUpActivity
import com.madkit.myapplication.activities.TaskListActivity
import com.madkit.myapplication.models.Board
import com.madkit.myapplication.models.User
import com.madkit.myapplication.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }
    }

    fun createBoard(activity: CreateBoardActivity, board:Board){
        mFireStore.collection(Constants.BOARDS).document().set(board, SetOptions.merge()).addOnSuccessListener {
            activity.boardCreatedSuccessfully()
        }.addOnFailureListener { exception->
            activity.hideProgressDialog()

        }
    }

    fun getBoardsList(activity:MainActivity){
        mFireStore.collection(Constants.BOARDS).whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId()).get().addOnSuccessListener { document->
            val boardList: ArrayList<Board> = ArrayList()
            for(i in document.documents){
                val board = i.toObject(Board::class.java)
                board?.documentId = i.id
                boardList.add(board!!)
            }
            activity.populateBoardsListToUI(boardList)
        }.addOnFailureListener { exception->
            activity.hideProgressDialog()

        }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId:String){
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener { document->
            val board = document.toObject(Board::class.java)!!
            board.documentId = document.id
            activity.boardDetails(board)
        }.addOnFailureListener { exception->
            activity.hideProgressDialog()

        }
    }
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener {document->
            val loggedInUser = document.toObject(User::class.java)!!
            when(activity){
                is SignInActivity ->  activity.signInSuccess(loggedInUser)
                is MainActivity -> activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                is MyProfileActivity-> activity.setUserDataInUI(loggedInUser)

            }


        }.addOnFailureListener {e->
            when(activity){
                is SignInActivity ->  activity.hideProgressDialog()
                is MainActivity -> activity.hideProgressDialog()
                is MyProfileActivity->activity.hideProgressDialog()
            }
        }
    }

    fun addUpdateTakList(activity: TaskListActivity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(taskListHashMap).addOnSuccessListener {
            activity.addUpdateTaskListSuccess()
        }.addOnFailureListener { e-> activity.hideProgressDialog() }
    }

    fun getCurrentUserId(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser!=null){
            currentUserId =currentUser.uid
        }
        return currentUserId
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
            activity.profileUpdateSuccess()
        }.addOnFailureListener { e-> activity.hideProgressDialog() }
    }

    fun getAssignedMemberListDetails(activity:MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS).whereIn(Constants.ID, assignedTo).get().addOnSuccessListener {document->
            val usersList: ArrayList<User> = ArrayList()
            for(i in document.documents){
                val user = i.toObject(User::class.java)!!
                usersList.add(user)
            }
            activity.setUpMemberList(usersList)

        }.addOnFailureListener { e-> activity.hideProgressDialog() }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        mFireStore.collection(Constants.USERS).whereEqualTo(Constants.EMAIL, email).get().addOnSuccessListener {
            document->
            if(document.documents.size>0){
                val user = document.documents[0].toObject(User::class.java)!!
                activity.memberDetails(user)
            }else{
                activity.hideProgressDialog()
                activity.showErrorSnackBar("No such member found")
            }
        }.addOnFailureListener { e-> activity.hideProgressDialog() }
    }

    fun assignedMemberToBoard(activity: MembersActivity, board:Board, user: User){
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS).document(board.documentId).update(assignedToHashMap).addOnSuccessListener {
            activity.memberAssignedSuccess(user)
        }.addOnFailureListener { e-> activity.hideProgressDialog() }
    }
}