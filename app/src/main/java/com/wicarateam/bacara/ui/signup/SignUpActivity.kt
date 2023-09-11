package com.wicarateam.bacara.ui.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.wicarateam.bacara.databinding.ActivitySignUpBinding
import com.wicarateam.bacara.ui.signin.SignInActivity
import com.wicarateam.bacara.ui.signin.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    private lateinit var email: String
    private lateinit var name: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var joinedDate: String
    private lateinit var subscription: String

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabaseReference = mFirebaseInstance.getReference("User")

        onClick()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onClick() {
        val currentDateTime = LocalDateTime.now()

        binding.backbutton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            username = binding.etUsername.text.toString()
            password = binding.etPassword.text.toString()
            email = binding.etEmail.text.toString()
            name = binding.etName.text.toString()
            joinedDate = currentDateTime.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
                .toString()
            subscription = "Non Premium User"

            if (username == "") {
                binding.etUsername.error = "Please input username"
                binding.etUsername.requestFocus()
            } else if (password == "") {
                binding.etPassword.error = "Please input password"
                binding.etPassword.requestFocus()
            } else if (name == "") {
                binding.etName.error = "Please input name"
                binding.etName.requestFocus()
            } else if (email == "") {
                binding.etName.error = "please input email"
                binding.etName.requestFocus()
            } else {
                val statusUsername = username.indexOf(".")
                if (statusUsername >= 0) {
                    binding.etUsername.error = "Please input username without ."
                    binding.etUsername.requestFocus()
                } else {
                    saveUser(username, password, name, email, joinedDate, subscription)
                }
            }
        }
    }

    private fun saveUser(
        username: String,
        password: String,
        name: String,
        email: String,
        joinedDate: String,
        subscription: String
    ) {
        val user = User()
        user.name = name
        user.email = email
        user.password = password
        user.username = username
        user.joinedDate = joinedDate
        user.subscription = subscription

        checkUserData(username, user)
    }

    private fun checkUserData(username: String, user: User) {
        mDatabaseReference.child(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dataUser = dataSnapshot.getValue(User::class.java)
                    if (dataUser == null) {
                        mDatabaseReference.child(username).setValue(user)
                        val intent = Intent(
                            this@SignUpActivity,
                            SignInActivity::class.java
                        )
                        startActivity(intent)
                        Toast.makeText(
                            this@SignUpActivity,
                            "Successfully Registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            "User is already registered",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@SignUpActivity, "" + error.message, Toast.LENGTH_LONG)
                        .show()
                }
            })
    }
}