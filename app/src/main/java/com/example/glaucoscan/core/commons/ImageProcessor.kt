package com.example.glaucoscan.core.commons

import android.graphics.Bitmap
import androidx.core.graphics.scale

object ImagePreprocessor {
    const val SIZE = 224

    /**
     * Meniru Resize((224,224)) di training: bitmap dikecilkan bertahap (÷2)
     */
    private fun resize(src: Bitmap): Bitmap {
        var cur = src
        while (cur.width >= SIZE * 2 && cur.height >= SIZE * 2) {
            val next = cur.scale(cur.width / 2, cur.height / 2)
            if (cur !== src) cur.recycle()
            cur = next
        }
        val out = cur.scale(SIZE, SIZE)
        if (cur !== src && cur !== out) cur.recycle()
        return out
    }

    /** ToTensor(): NCHW, RGB, ÷255, tanpa mean/std. */
    fun toNchw(src: Bitmap): FloatArray {
        val resized = resize(src)
        val pixels = IntArray(SIZE * SIZE)
        resized.getPixels(pixels, 0, SIZE, 0, 0, SIZE, SIZE)
        if (resized !== src) resized.recycle()

        val plane = SIZE * SIZE
        val out = FloatArray(3 * plane)
        for (i in 0 until plane) {
            val p = pixels[i]
            out[i] = ((p shr 16) and 0xFF) / 255f
            out[plane + i] = ((p shr 8) and 0xFF) / 255f
            out[2 * plane + i] = (p and 0xFF) / 255f
        }
        return out
    }
}