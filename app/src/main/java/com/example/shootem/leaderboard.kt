package com.example.shootem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class leaderboard : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Leaderboard", Context.MODE_PRIVATE)

        // Retrieve saved points from SharedPreferences
        val savedPoints = getSavedPoints()

        // Sort the saved points in descending order
        val sortedPoints = savedPoints.sortedDescending()

        // Find the TextViews for displaying scores
        val score1Text = findViewById<TextView>(R.id.score1_text)
        val score2Text = findViewById<TextView>(R.id.score2_text)
        val score3Text = findViewById<TextView>(R.id.score3_text)

        // Display the sorted points in the TextViews
        if (sortedPoints.isNotEmpty()) {
            score1Text.text = sortedPoints.getOrNull(0).toString()
            score2Text.text = sortedPoints.getOrNull(1).toString()
            score3Text.text = sortedPoints.getOrNull(2).toString()
        }

        // Find the exit button ImageView
        val scoreButton = findViewById<ImageView>(R.id.exit_button)

        // Set OnClickListener for the exit button
        scoreButton.setOnClickListener {
            // Create an Intent to start the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to retrieve saved points from SharedPreferences
    private fun getSavedPoints(): List<Int> {
        val pointsString = sharedPreferences.getString("points", "")
        return if (!pointsString.isNullOrEmpty()) {
            pointsString.split(",").map { it.toInt() }
        } else {
            emptyList()
        }
    }
}
