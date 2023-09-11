package com.wicarateam.bacara.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivityHomeBinding
import com.wicarateam.bacara.ui.camera.DetectorActivity
import com.wicarateam.bacara.ui.library.LibraryActivity
import com.wicarateam.bacara.ui.playgame.PlayGameActivity
import com.wicarateam.bacara.ui.profile.ProfileActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigation()

        clickMenu()
    }

    private fun clickMenu() {
        binding.ivGame.setOnClickListener {
            val intent = Intent(this@HomeActivity, PlayGameActivity::class.java)
            startActivity(intent)
        }
        binding.ivReport.setOnClickListener {
            sendMailTo(
                "B21-CAP0027@bangkit.academy",
                "Report Bug",
                "Hi I find a bug on your app, here's the detail...."
            )
        }
        binding.ivLibrary.setOnClickListener {
            val intent = Intent(this@HomeActivity, LibraryActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun sendMailTo(address: String, subject: String, text: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            val mailto = "mailto:$address"
            data = Uri.parse(mailto)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.camera -> {
                    startActivity(Intent(applicationContext, DetectorActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NO_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NO_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    startActivity(Intent(applicationContext, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NO_HISTORY)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }
}