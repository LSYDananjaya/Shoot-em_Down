package com.example.shootem

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        // Find the score button ImageView
        val scoreButton = findViewById<ImageView>(R.id.score_button)

        // Set OnClickListener for the score button
        scoreButton.setOnClickListener {
            // Create an Intent to start the LeaderboardActivity
            val intent = Intent(this, leaderboard::class.java)
            startActivity(intent)
        }

        // Find the play button ImageView
        val playButton = findViewById<ImageView>(R.id.play_button)

        // Set OnClickListener for the play button
        playButton.setOnClickListener {
            // Call the startGame function
            startGame()
        }
    }

    // Function to start the game
    private fun startGame() {
        startActivity(Intent(this, Game::class.java))
        finish() // Optional: Finish the MainActivity if you don't want it to remain in the back stack
    }


    }

