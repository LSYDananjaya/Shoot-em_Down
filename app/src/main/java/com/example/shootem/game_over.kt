package com.example.shootem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class game_over : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)

        // Get the points from the intent extras
        val points = intent.extras?.getInt("points", 0)

        // Find the scoreDisplay TextView
        val scoreDisplay = findViewById<TextView>(R.id.scoreDisplay)
        scoreDisplay.text = points.toString()

        // Save the points to SharedPreferences
        savePoints(points)

        // Find the exit button ImageView
        val scoreButton = findViewById<ImageView>(R.id.exit)

        // Set OnClickListener for the exit button
        scoreButton.setOnClickListener {
            // Create an Intent to start the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Find the play button ImageView
        val playButton = findViewById<ImageView>(R.id.replay)

        // Set OnClickListener for the play button
        playButton.setOnClickListener {
            // Call the startGame function
            startGame()
        }
    }

    // Function to save points to SharedPreferences
    private fun savePoints(points: Int?) {
        points?.let {
            val editor = sharedPreferences.edit()
            val savedPoints = sharedPreferences.getString("points", "")
            val updatedPoints = if (savedPoints.isNullOrEmpty()) {
                points.toString()
            } else {
                "$savedPoints,$points"
            }
            editor.putString("points", updatedPoints)
            editor.apply()
        }
    }

    // Function to start the game
    private fun startGame() {
        startActivity(Intent(this, Game::class.java))
        finish() // Optional: Finish the MainActivity if you don't want it to remain in the back stack
    }
}
