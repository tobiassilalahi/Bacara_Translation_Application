package com.wicarateam.bacara.ui.signin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.wicarateam.bacara.databinding.ActivitySignInBinding
import com.wicarateam.bacara.ui.home.HomeActivity
import com.wicarateam.bacara.ui.signup.SignUpActivity
import com.wicarateam.bacara.utils.Preferences


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    private lateinit var username: String
    private lateinit var password: String

    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = Preferences(this)

        mDatabase = FirebaseDatabase.getInstance().getReference("User")

        binding.btnRegister.setOnClickListener {
            val mIntent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(mIntent)
        }

        binding.btnSignin.setOnClickListener {
            username = binding.etUsername.text.toString()
            password = binding.etPassword.text.toString()

            when {
                username == "" -> {
                    binding.etUsername.error = "Please input your username"
                    binding.etUsername.requestFocus()
                }
                password == "" -> {
                    binding.etPassword.error = "Please input your password"
                    binding.etPassword.requestFocus()
                }
                else -> {
                    login(username, password)
                }
            }
        }
    }

    private fun login(username: String, password: String) {
        mDatabase.child(username).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user == null) {
                    Toast.makeText(this@SignInActivity, "User not found", Toast.LENGTH_LONG).show()
                } else {
                    if (user.password.equals(password)) {
                        Toast.makeText(this@SignInActivity, "Welcome to Bacara", Toast.LENGTH_LONG)
                            .show()

                        preferences.setValues("name", user.name.toString())
                        preferences.setValues("username", user.username.toString())
                        preferences.setValues("email", user.email.toString())
                        preferences.setValues("subscription", user.subscription.toString())
                        preferences.setValues("joinedDate", user.joinedDate.toString())

                        finishAffinity()

                        val intent = Intent(this@SignInActivity, HomeActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this@SignInActivity, "Wrong Password", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignInActivity, "" + error.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}