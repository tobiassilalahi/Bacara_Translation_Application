package com.wicarateam.bacara.ui.playgame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.databinding.ActivityPlayGameBinding
import com.wicarateam.bacara.ui.home.HomeActivity

class PlayGameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayGameBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.backbutton.setOnClickListener {
            val intent = Intent(this@PlayGameActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}