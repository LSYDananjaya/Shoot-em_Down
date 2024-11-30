package com.example.shootem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class enemyShip (var context: Context) {
    var enemySpaceship: Bitmap
    var ex: Int
    var ey = 0
    var enemyVelocity: Int
    var random: Random

    init {
        enemySpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.rocket2)
        random = Random()
        ex = 200 + random.nextInt(400)
        enemyVelocity = 14 + random.nextInt(10)
    }

    val enemySpaceshipWidth: Int
        get() = enemySpaceship.getWidth()
    val enemySpaceshipHeight: Int
        get() = enemySpaceship.getHeight()
}