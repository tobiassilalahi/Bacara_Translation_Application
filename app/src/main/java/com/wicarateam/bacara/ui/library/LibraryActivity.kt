package com.wicarateam.bacara.ui.library

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.wicarateam.bacara.R
import com.wicarateam.bacara.databinding.ActivityLibraryBinding
import com.wicarateam.bacara.ui.home.HomeActivity

class LibraryActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = findViewById<WebView>(R.id.webView)
        webView.loadUrl("https://pmpk.kemdikbud.go.id/sibi/pencarian")

        binding.fab.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            if (view.id == R.id.fab) {
                val intent = Intent(this@LibraryActivity, HomeActivity::class.java)
                startActivity(intent)
            }
        }
    }
}