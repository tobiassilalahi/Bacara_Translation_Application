package com.wicarateam.bacara.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivityThirdOnboardingBinding
import com.wicarateam.bacara.ui.signin.SignInActivity

class ThirdOnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_onboarding)

        binding = ActivityThirdOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            val mIntent = Intent(this@ThirdOnboardingActivity, SignInActivity::class.java)
            startActivity(mIntent)
        }
    }
}