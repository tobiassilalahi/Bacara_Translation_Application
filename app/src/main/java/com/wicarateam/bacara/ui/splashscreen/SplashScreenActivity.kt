package com.wicarateam.bacara.ui.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.R
import com.wicarateam.bacara.ui.onboarding.FirstOnboardingActivity

class SplashScreenActivity : AppCompatActivity() {
    private val splashTime: Long = 700
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, FirstOnboardingActivity::class.java))
            finish()
        }, splashTime)
    }
}