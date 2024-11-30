package com.example.shootem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Explosion(context: Context, eX: Int, eY: Int) {
    var explosion = arrayOfNulls<Bitmap>(9)
    var explosionFrame: Int
    var eX: Int
    var eY: Int

    init {
        explosion[0] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion0
        )
        explosion[1] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion1
        )
        explosion[2] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion2
        )
        explosion[3] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion3
        )
        explosion[4] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion4
        )
        explosion[5] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion5
        )
        explosion[6] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion6
        )
        explosion[7] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion7
        )
        explosion[8] = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.explosion8
        )
        explosionFrame = 0
        this.eX = eX
        this.eY = eY
    }

    fun getExplosion(explosionFrame: Int): Bitmap? {
        return explosion[explosionFrame]
    }
}