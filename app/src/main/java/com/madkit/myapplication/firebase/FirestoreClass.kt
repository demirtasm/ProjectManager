package com.madkit.myapplication.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.madkit.myapplication.activities.SignInActivity
import com.madkit.myapplication.activities.SignUpActivity
import com.madkit.myapplication.models.User
import com.madkit.myapplication.utils.Constants

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
            activity.userRegisteredSuccess()
        }
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).get().addOnSuccessListener {document->
            val loggedInUser = document.toObject(User::class.java)!!
            if(loggedInUser != null){
                activity.signInSuccess(loggedInUser)
            }
        }
    }

    fun getCurrentUserId(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser!=null){
            currentUserId =currentUser.uid
        }
        return currentUserId
    }
}