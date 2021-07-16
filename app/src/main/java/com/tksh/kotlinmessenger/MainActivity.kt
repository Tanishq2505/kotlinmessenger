package com.tksh.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


private val firebaseAuth = FirebaseAuth.getInstance()
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val userNameEditText = findViewById<EditText>(R.id.username_edit_text_register)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text_register)
        val emailEditText = findViewById<EditText>(R.id.email_edit_text_register)
        val registerButton = findViewById<Button>(R.id.register_button_register)
        val alreadyRegistered = findViewById<TextView>(R.id.already_have_account_text_view_register)

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
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.message,Toast.LENGTH_LONG).show()
                }
        }
    }

}