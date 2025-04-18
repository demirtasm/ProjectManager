package com.madkit.myapplication.activities

import android.Manifest
import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.madkit.myapplication.R
import com.madkit.myapplication.databinding.ActivityMyProfileBinding
import com.madkit.myapplication.firebase.FirestoreClass
import com.madkit.myapplication.models.Board
import com.madkit.myapplication.models.User
import com.madkit.myapplication.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding


    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mUserDetails: User
    private var mProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        FirestoreClass().loadUserData(this)

        binding.ivUserImage.setOnClickListener {
            imageChooserForGallery()

        }
        binding.btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "Ooops, you just denied the permission for storage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data != null) {
            mSelectedImageFileUri = data.data
            try {
                Glide.with(this@MyProfileActivity).load(mSelectedImageFileUri).centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder).into(binding.ivUserImage)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun updateUserProfileData() {
        val userHasMap = HashMap<String, Any>()
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHasMap["image"]
            userHasMap[Constants.IMAGE] = mProfileImageURL
        }
        if(binding.etName.text.toString() != mUserDetails.name){
            userHasMap[Constants.NAME] = binding.etName.text.toString()
        }
        if(binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHasMap[Constants.MOBILE] = binding.etMobile.text.toString()
        }
        FirestoreClass().updateUserProfileData(this, userHasMap)
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user
        Glide.with(this@MyProfileActivity).load(user.image).centerCrop()
            .placeholder(R.drawable.ic_user_place_holder).into(binding.ivUserImage)
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }
    }

    private fun setUpActionBar() {

        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)
        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(
                    mSelectedImageFileUri
                )
            )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this@MyProfileActivity, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }


    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }
}