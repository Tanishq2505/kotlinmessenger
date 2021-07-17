package com.tksh.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


private val firebaseAuth = FirebaseAuth.getInstance()
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val userNameEditText = findViewById<EditText>(R.id.username_edit_text_register)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text_register)
        val emailEditText = findViewById<EditText>(R.id.email_edit_text_register)
        val registerButton = findViewById<Button>(R.id.register_button_register)
        val alreadyRegistered = findViewById<TextView>(R.id.already_have_account_text_view_register)
        val selectPhotoButton = findViewById<Button>(R.id.select_image_button_register)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val userName = userNameEditText.text.toString().trim()
            performRegister(userName,email,password)
        }
        alreadyRegistered.setOnClickListener {
            Log.d("MainActivity","Try to show log in activity")
            startActivity(Intent(this,LoginActivity::class.java))

        }
        selectPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
var selectedPhotoUri : Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data !=null){
            data.data.let { uri ->
                launchImageCrop(uri)
            }

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            select_image_button_register.setBackgroundDrawable(bitmapDrawable)
        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if(resultCode == Activity.RESULT_OK){
                selectedPhotoUri = result.uri
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
                photo_image_view.setImageBitmap(bitmap)
                select_image_button_register.alpha = 0f

            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Log.d("RegisterActivity","Crop Error = ${result.error}")
            }
        }


    }

    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1080,1080)
            .setFixAspectRatio(true)
            .start(this)
    }

    private fun performRegister(userName :String,email:String,password:String) {


        if(email == ""){
            Toast.makeText(this,"Please enter email!",Toast.LENGTH_LONG).show()
            return
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Please enter valid email!",Toast.LENGTH_LONG).show()
            return
        }else if(password == ""){
            Toast.makeText(this,"Please enter password!",Toast.LENGTH_LONG).show()
            return
        }else{
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this,"Sign Up Successful! = ${it.result?.user?.uid}",Toast.LENGTH_LONG).show()
                    uploadImageToStorage()

                }
                .addOnFailureListener{
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadImageToStorage() {
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
    val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
    ref.putFile(selectedPhotoUri!!)
        .addOnSuccessListener {
            Log.d("RegisterActivity","Photo Uploaded = ${it.metadata?.path}")
            ref.downloadUrl.addOnSuccessListener { uri ->
                Log.d("RegisterActivity","Downloaded URI = $uri")
                saveUserToDatabase(uri.toString())
            }
        }
        .addOnFailureListener{
            Log.d("RegisterActivity","Error in photo Upload ${it.message}")
        }
    }

    private fun saveUserToDatabase(profileImageUrl: String) {
        val cUser = FirebaseAuth.getInstance().uid?:""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$cUser")
        val user = User(cUser,username_edit_text_register.text.toString(),profileImageUrl,email_edit_text_register.text.toString())
        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Data send successful!")
            }
            .addOnFailureListener{
                Log.d("RegisterActivity","Data Send Failed = ${it.message}")
            }
    }

}

class User(val uid :String,val username:String, val profileImageUrl:String,val email:String){
    constructor():this("","","","")
}