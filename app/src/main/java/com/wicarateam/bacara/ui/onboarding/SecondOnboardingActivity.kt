package com.wicarateam.bacara.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivitySecondOnboardingBinding
import com.wicarateam.bacara.ui.signin.SignInActivity

class SecondOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_onboarding)

        binding = ActivitySecondOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNext.setOnClickListener {
            val mIntent = Intent(this@SecondOnboardingActivity, ThirdOnboardingActivity::class.java)
            startActivity(mIntent)
        }

        binding.btnSkip.setOnClickListener {
            val mIntent = Intent(this@SecondOnboardingActivity, SignInActivity::class.java)
            startActivity(mIntent)
        }
    }
}