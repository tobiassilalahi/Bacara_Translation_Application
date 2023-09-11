package com.wicarateam.bacara.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivityProfileBinding
import com.wicarateam.bacara.ui.camera.DetectorActivity
import com.wicarateam.bacara.ui.home.HomeActivity
import com.wicarateam.bacara.utils.Preferences

class ProfileActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()

        preferences = Preferences(this@ProfileActivity.applicationContext)

        binding.tvName.text = preferences.getValues("name")
        binding.tvEmail.text = preferences.getValues("email")
        binding.tvSubscription.text = preferences.getValues("subscription")
        binding.tvJoinDate.text = preferences.getValues("joinedDate")
        binding.tvUsername.text = preferences.getValues("username")
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.profile
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.camera -> {
                    startActivity(Intent(applicationContext, DetectorActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}