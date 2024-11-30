package com.example.shootem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.util.Random

class ourShip (var context: Context) {
    var ourSpaceship: Bitmap
    var ox: Int
    var oy: Int
    var random: Random

    init {
        ourSpaceship = BitmapFactory.decodeResource(context.resources, R.drawable.rocket1)
        random = Random()
        ox = random.nextInt(shooter.screenWidth)
        oy = shooter.screenHeight - ourSpaceship.getHeight()
    }

    val ourSpaceshipWidth: Int
        get() = ourSpaceship.getWidth()
}