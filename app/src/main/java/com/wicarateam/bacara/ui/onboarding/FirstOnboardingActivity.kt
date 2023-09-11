package com.wicarateam.bacara.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivityFirstOnboardingBinding
import com.wicarateam.bacara.ui.signin.SignInActivity

class FirstOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_onboarding)

        binding = ActivityFirstOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val mIntent = Intent(this@FirstOnboardingActivity, SecondOnboardingActivity::class.java)
            startActivity(mIntent)
        }

        binding.btnSkip.setOnClickListener {
            val mIntent = Intent(this@FirstOnboardingActivity, SignInActivity::class.java)
            startActivity(mIntent)
        }
    }
}