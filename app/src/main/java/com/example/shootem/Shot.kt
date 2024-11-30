package com.example.shootem

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class Shot (var context: Context, var shx: Int, var shy: Int) {
    var shot: Bitmap

    init {
        shot = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.shot
        )
    }

    val shotWidth: Int
    get() = shot.getWidth()
    val shotHeight: Int
    get() = shot.getHeight()
}