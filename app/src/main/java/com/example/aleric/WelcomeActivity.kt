package com.example.aleric

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    private lateinit var welcomeImage: ImageView
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        startButton = findViewById(R.id.startButton)
        welcomeText = findViewById(R.id.welcomeText)

        startButton.setOnClickListener {
            // Mark the app as having been opened before
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            prefs.edit().putBoolean("is_first_run", false).apply()

            // Start the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Close the WelcomeActivity
        }
    }
}
