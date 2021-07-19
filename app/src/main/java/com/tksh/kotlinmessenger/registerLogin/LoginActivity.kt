package com.tksh.kotlinmessenger.registerLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.tksh.kotlinmessenger.R
import com.tksh.kotlinmessenger.messages.LatestMessagesActivity

private val firebaseAuth = FirebaseAuth.getInstance()
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"
        val emailEditText = findViewById<EditText>(R.id.email_edit_text_login)
        val passwordEditText = findViewById<EditText>(R.id. password_edit_text_login)
        val loginButton = findViewById<Button>(R.id.login_button_login)

        loginButton.setOnClickListener {
            val emailLogin = emailEditText.text.toString()
            val passwordLogin = passwordEditText.text.toString()
            Log.d("LoginActivity","Email = $emailLogin")
            Log.d("LoginActivity","Password = $passwordLogin")
            performLogin(emailLogin,passwordLogin)


        }
    }

    private fun performLogin(emailLogin: String, passwordLogin: String) {
        firebaseAuth.signInWithEmailAndPassword(emailLogin,passwordLogin)
            .addOnSuccessListener {
                val intent = Intent(this,LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this,"${it.message}",Toast.LENGTH_LONG).show()
            }
            }
}