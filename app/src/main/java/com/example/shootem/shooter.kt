package com.example.shootem

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Typeface
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.Random

class shooter(private val mContext: Context) : View(mContext) {
    private var mHandler: Handler? = null
    private lateinit var background: Bitmap
    private lateinit var lifeImage: Bitmap


    private var UPDATE_MILLIS: Long = 20 // Decreased update interval for faster shooting
    private var points = 0
    private var life = 3
    private var scorePaint: Paint
    private var TEXT_SIZE = 80
    private var paused = false
    private var ourSpaceship: ourShip
    private var enemySpaceship: enemyShip
    private var random: Random
    private var enemyShots: ArrayList<Shot>
    private var ourShots: ArrayList<Shot>
    private var explosion: Explosion? = null
    private var explosions: ArrayList<Explosion>
    private var lastEnemyShotTime = 0L // Track the time of the last enemy shot
    private var lastTapTime = 0L
    private var rapidFireEnabled = false
    private var RAPID_FIRE_INTERVAL = 500L // Interval for rapid fire in milliseconds
    private var ENEMY_SHOT_INTERVAL: Long = 1500 // Interval between enemy shots in milliseconds
    var difficultyLevel = 1
    val LEVEL_2_THRESHOLD = 30
    val LEVEL_3_THRESHOLD = 150
    val EXTRA_LIFE_PER_LEVEL = 1
    val POINT_MULTIPLIER_LEVEL_2 = 2
    val POINT_MULTIPLIER_LEVEL_3 = 3

    private var highScore = 0 // Track the highest score achieved

    private val multiplier = arrayOf(1, 2, 3) // Multipliers based on difficulty level

    private val runnable = object : Runnable {
        override fun run() {
            invalidate()
            if (!paused) mHandler?.postDelayed(this, UPDATE_MILLIS)
        }
    }

    init {
        val display = (context as Activity).windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        screenWidth = size.x
        screenHeight = size.y
        random = Random()
        enemyShots = ArrayList()
        ourShots = ArrayList()
        explosions = ArrayList()
        ourSpaceship = ourShip(context)
        enemySpaceship = enemyShip(context)
        scorePaint = Paint()
        scorePaint.color = Color.RED
        scorePaint.textSize = TEXT_SIZE.toFloat()
        scorePaint.textAlign = Paint.Align.LEFT
        mHandler = Handler()
        mHandler?.post(runnable)
        background = BitmapFactory.decodeResource(context.resources, R.drawable.background)
        lifeImage = BitmapFactory.decodeResource(context.resources, R.drawable.life)
    }

    override fun onDraw(canvas: Canvas) {
        // Draw background, points, and life
        canvas.drawBitmap(background, 0f, 0f, null)
        // Define paint for drawing text with specific attributes
        val textPaint = Paint().apply {
            color = Color.parseColor("#2196F3") // Blue color for points
            textSize = 75f // Increased textSize for points
            textAlign = Paint.Align.LEFT
            typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL) // Custom font for points
        }


        canvas.drawText("Pt: $points", 20f, TEXT_SIZE.toFloat(), textPaint)


        // Define paint for multiplier and level text
        val multiplierTextPaint = Paint().apply {
            color = Color.parseColor("#FF5722") // Orange color
            textSize = 75f // Increased textSize
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create("serif-monospace", Typeface.BOLD) // Custom font for multiplier
        }

        val levelTextPaint = Paint().apply {
            color = Color.parseColor("#4CAF50") // Green color
            textSize = 90f // Increased textSize
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create("cursive", Typeface.BOLD) // Custom font for level
        }


        // Draw point multiplier and difficulty level with adjusted positions
        val textMargin = 20f // Margin between text and screen edge
        val textYOffset = TEXT_SIZE.toFloat() + 40 // Increased offset for higher position
        val textSpacing = 80f // Increased spacing between texts
        canvas.drawText("Multiplier: x${multiplier[difficultyLevel - 1]}", screenWidth.toFloat() - textMargin, textYOffset, multiplierTextPaint)
        canvas.drawText("Level: $difficultyLevel", screenWidth.toFloat() - textMargin, textYOffset + textSpacing, levelTextPaint)

        for (i in life downTo 1) {
            canvas.drawBitmap(
                lifeImage,
                (screenWidth - lifeImage.width * i).toFloat(),
                0f,
                null
            )
        }

        // Check for difficulty level and adjust gameplay
        if (points >= LEVEL_3_THRESHOLD && difficultyLevel < 3) {
            // Move to difficulty level 3
            difficultyLevel = 3
            life += EXTRA_LIFE_PER_LEVEL
            applyPointMultiplier(POINT_MULTIPLIER_LEVEL_3)
            showNotification("Difficulty level increased to $difficultyLevel. Point multiplier: $POINT_MULTIPLIER_LEVEL_3")
        } else if (points >= LEVEL_2_THRESHOLD && difficultyLevel < 2) {
            // Move to difficulty level 2
            difficultyLevel = 2
            life += EXTRA_LIFE_PER_LEVEL
            applyPointMultiplier(POINT_MULTIPLIER_LEVEL_2)
            showNotification("Difficulty level increased to $difficultyLevel. Point multiplier: $POINT_MULTIPLIER_LEVEL_2")
        }

        // Handle game over
        if (life <= 0) {
            paused = true
            mHandler?.removeCallbacks(runnable) // Stop the game loop
            if (points > highScore) {
                highScore = points
                // Show new high score reached
                showHighScore()
            }
            val intent = Intent(context, game_over::class.java)
            intent.putExtra("points", points)
            context.startActivity(intent)
            (context as Activity).finish()
            return
        }

        // Move enemy spaceship
        enemySpaceship.ex += enemySpaceship.enemyVelocity

        // Check if the enemy spaceship has hit a boundary and switch direction if necessary
        if (enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth >= screenWidth) {
            // Change direction to left when reaching the right edge
            enemySpaceship.enemyVelocity *= -1 // Switch direction
            enemySpaceship.ex = screenWidth - enemySpaceship.enemySpaceshipWidth // Adjust position
        } else if (enemySpaceship.ex <= 0) {
            // Change direction to right when reaching the left edge
            enemySpaceship.enemyVelocity *= -1 // Switch direction
            enemySpaceship.ex = 0 // Adjust position
        }

        // Check if it's time for the enemy ship to fire
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastEnemyShotTime >= ENEMY_SHOT_INTERVAL) {
            fireEnemyShot()
            lastEnemyShotTime = currentTime
        }

        // Draw enemy spaceship
        canvas.drawBitmap(
            enemySpaceship.enemySpaceship,
            enemySpaceship.ex.toFloat(),
            enemySpaceship.ey.toFloat(),
            null
        )

        // Draw our spaceship
        if (ourSpaceship.ox > screenWidth - ourSpaceship.ourSpaceshipWidth) {
            ourSpaceship.ox = screenWidth - ourSpaceship.ourSpaceshipWidth
        } else if (ourSpaceship.ox < 0) {
            ourSpaceship.ox = 0
        }
        canvas.drawBitmap(
            ourSpaceship.ourSpaceship,
            ourSpaceship.ox.toFloat(),
            ourSpaceship.oy.toFloat(),
            null
        )

        // Handle enemy shots
        val iterator = enemyShots.iterator()
        while (iterator.hasNext()) {
            val shot = iterator.next()
            shot.shy += 20 // Increased speed of enemy shots
            canvas.drawBitmap(
                shot.shot,
                shot.shx.toFloat(),
                shot.shy.toFloat(),
                null
            )
            if (shot.shx >= ourSpaceship.ox && shot.shx <= ourSpaceship.ox + ourSpaceship.ourSpaceshipWidth &&
                shot.shy >= ourSpaceship.oy && shot.shy <= screenHeight
            ) {
                life -= 1 // Reduced life by 1 when shot hits
                iterator.remove() // Use iterator to safely remove the shot
                explosion = Explosion(context, ourSpaceship.ox, ourSpaceship.oy)
                explosions.add(explosion!!)
            } else if (shot.shy >= screenHeight) {
                iterator.remove() // Use iterator to safely remove the shot
            }
        }

        // Handle our shots
        val ourShotsIterator = ourShots.iterator()
        while (ourShotsIterator.hasNext()) {
            val ourShot = ourShotsIterator.next()
            ourShot.shy -= 15 * multiplier[difficultyLevel - 1] // Apply multiplier based on difficulty level
            canvas.drawBitmap(
                ourShot.shot,
                ourShot.shx.toFloat(),
                ourShot.shy.toFloat(),
                null
            )
            if (ourShot.shx >= enemySpaceship.ex && ourShot.shx <= enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth &&
                ourShot.shy <= enemySpaceship.enemySpaceshipWidth && ourShot.shy >= enemySpaceship.ey
            ) {
                points += 1 * multiplier[difficultyLevel - 1] // Increment points with multiplier
                ourShotsIterator.remove()
                explosion = Explosion(context, enemySpaceship.ex, enemySpaceship.ey)
                explosions.add(explosion!!)
            } else if (ourShot.shy <= 0) {
                ourShotsIterator.remove()
            }
        }

        // Handle explosions
        val explosionsIterator = explosions.iterator()
        while (explosionsIterator.hasNext()) {
            val explosion = explosionsIterator.next()
            canvas.drawBitmap(
                explosion.getExplosion(explosion.explosionFrame)!!,
                explosion.eX.toFloat(),
                explosion.eY.toFloat(),
                null
            )
            explosion.explosionFrame++
            if (explosion.explosionFrame > 8) {
                explosionsIterator.remove()
            }
        }
    }

    // Method to fire enemy shot
    private fun fireEnemyShot() {
        val enemyShot = Shot(
            context,
            enemySpaceship.ex + enemySpaceship.enemySpaceshipWidth / 2,
            enemySpaceship.ey
        )
        enemyShots.add(enemyShot)
    }

    // Method to show new high score reached
    private fun showHighScore() {
        val sharedPreferences = mContext.getSharedPreferences("HighScore", Context.MODE_PRIVATE)
        val previousHighScore = sharedPreferences.getInt("highScore", 0)

        if (points > previousHighScore) {
            // If the current score is higher than the previous high score, update and display the new high score
            val editor = sharedPreferences.edit()
            editor.putInt("highScore", points)
            editor.apply()

            val message = "New High Score: $points"
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDifficultyLevelNotification(level: Int) {
        // Implement UI logic to show a notification about the new difficulty level
        // For example, you can show a Toast message or update a TextView on the game screen
        val message = "Difficulty level increased to $level"
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun applyPointMultiplier(multiplier: Int) {
        points *= multiplier
    }

    private fun showNotification(message: String) {
        // Display a Toast message with the given message
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x.toInt()
        if (event.action == MotionEvent.ACTION_UP) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime < RAPID_FIRE_INTERVAL) {
                rapidFireEnabled = true
            }
            lastTapTime = currentTime
            if (ourShots.size < 1 || rapidFireEnabled) {
                val ourShot = Shot(
                    context,
                    ourSpaceship.ox + ourSpaceship.ourSpaceshipWidth / 2,
                    ourSpaceship.oy
                )
                ourShots.add(ourShot)
                if (rapidFireEnabled) {
                    Handler().postDelayed({ rapidFireEnabled = false }, RAPID_FIRE_INTERVAL)
                }
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX
        }
        if (event.action == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX
        }
        return true
    }

    companion object {
        var screenWidth: Int = 0
        var screenHeight: Int = 0
    }
}
