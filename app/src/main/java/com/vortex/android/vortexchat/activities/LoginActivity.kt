package com.vortex.android.vortexchat.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vortex.android.vortexchat.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
    }
}