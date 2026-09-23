package com.example.glaucoscan.core.commons

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.FloatBuffer
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp
import androidx.core.graphics.scale
import androidx.core.graphics.get

@Singleton
class ValidatorHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val env = OrtEnvironment.getEnvironment()
    private val lock = Mutex()

    private val session: OrtSession by lazy {
        val bytes = context.assets.open(ASSET).use { it.readBytes() }
        OrtSession.SessionOptions().use { env.createSession(bytes, it) }
    }
    private val inputName: String by lazy { session.inputNames.first() }

    suspend fun isFundus(bitmap: Bitmap): Boolean = score(bitmap) >= FUNDUS_THRESHOLD

    suspend fun score(bitmap: Bitmap): Float = withContext(Dispatchers.Default) {
        lock.withLock {
            val size = ImagePreprocessor.SIZE.toLong()
            val chw = ImagePreprocessor.toNchw(bitmap)
            OnnxTensor.createTensor(env, FloatBuffer.wrap(chw), longArrayOf(1, 3, size, size)).use { input ->
                session.run(mapOf(inputName to input)).use { out ->
                    val logit = (out[0] as OnnxTensor).floatBuffer.get(0)
                    1f / (1f + exp(-logit))
                }
            }
        }
    }

    fun looksLikeFundus(bitmap: Bitmap): Boolean {
        val s = bitmap.scale(64, 64)
        var r = 0L; var g = 0L; var b = 0L
        for (y in 0 until 64) for (x in 0 until 64) {
            val p = s[x, y]
            r += Color.red(p); g += Color.green(p); b += Color.blue(p)
        }
        if (s !== bitmap) s.recycle()
        return r > g * 1.3 && g > b * 1.2
    }

    private companion object {
        const val ASSET = "models/fundus_validator.onnx"
        const val FUNDUS_THRESHOLD = 0.9f
    }
}