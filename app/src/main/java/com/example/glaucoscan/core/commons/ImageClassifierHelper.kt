package com.example.glaucoscan.core.commons

import ai.onnxruntime.OnnxJavaType
import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Half
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.GlaucomaLabel
import com.example.glaucoscan.domain.models.ModelConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.inject.Singleton
import kotlin.math.exp


@Singleton
class ImageClassifierHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : AutoCloseable {

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val lock = Mutex()

    private var session: OrtSession? = null
    private var loaded: ModelConfig? = null
    private var inputName = ""
    private var inputType = OnnxJavaType.FLOAT

    suspend fun classify(bitmap: Bitmap, model: ModelConfig): ClassificationResult =
        withContext(Dispatchers.Default) {
            lock.withLock {
                val session = ensureSession(model)
                val chw = ImagePreprocessor.toNchw(bitmap)

                createInput(chw).use { input ->
                    val t0 = System.nanoTime()
                    session.run(mapOf(inputName to input)).use { output ->
                        val latencyMs = (System.nanoTime() - t0) / 1_000_000f
                        val raw = (output[0] as OnnxTensor).floatBuffer.get(0)

                        val p = if (OUTPUT_IS_LOGIT) sigmoid(raw) else raw
                        val pGlaucoma = if (GLAUCOMA_IS_POSITIVE_CLASS) p else 1f - p

                        ClassificationResult(
                            glaucomaProbability = pGlaucoma,
                            label = if (pGlaucoma >= GLAUCOMA_THRESHOLD) GlaucomaLabel.GLAUCOMA else GlaucomaLabel.NORMAL,
                            latencyMs = latencyMs,
                            inputPrecision = if (inputType == OnnxJavaType.FLOAT16) "FP16" else "FP32",
                            model = model,
                        )
                    }
                }
            }
        }

    private fun ensureSession(model: ModelConfig): OrtSession {
        session?.let { if (loaded == model) return it }
        session?.close()
        session = null
        loaded = null

        val bytes = context.assets.open(model.assetPath).use { it.readBytes() }
        val created = OrtSession.SessionOptions().use { opts ->
            opts.setIntraOpNumThreads(Runtime.getRuntime().availableProcessors().coerceIn(2, 4))
            opts.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            env.createSession(bytes, opts)
        }

        val (name, nodeInfo) = created.inputInfo.entries.first()
        inputName = name
        inputType = (nodeInfo.info as TensorInfo).type

        session = created
        loaded = model
        return created
    }

    @SuppressLint("HalfFloat")
    private fun createInput(chw: FloatArray): OnnxTensor {
        val shape = longArrayOf(1, 3, ImagePreprocessor.SIZE.toLong(), ImagePreprocessor.SIZE.toLong())
        if (inputType != OnnxJavaType.FLOAT16) {
            return OnnxTensor.createTensor(env, FloatBuffer.wrap(chw), shape)
        }
        val bytes = ByteBuffer.allocateDirect(chw.size * 2).order(ByteOrder.nativeOrder())
        val halves = bytes.asShortBuffer()
        for (v in chw) halves.put(Half.toHalf(v))
        return OnnxTensor.createTensor(env, bytes, shape, OnnxJavaType.FLOAT16)
    }

    private fun sigmoid(x: Float) = 1f / (1f + exp(-x))

    override fun close() {
        session?.close()
        session = null
        loaded = null
    }

    private companion object {
        const val OUTPUT_IS_LOGIT = true
        const val GLAUCOMA_IS_POSITIVE_CLASS = false
        const val GLAUCOMA_THRESHOLD = 0.5f
    }
}