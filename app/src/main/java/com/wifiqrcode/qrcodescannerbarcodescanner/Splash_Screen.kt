package com.wifiqrcode.qrcodescannerbarcodescanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        },1000)
    }
}