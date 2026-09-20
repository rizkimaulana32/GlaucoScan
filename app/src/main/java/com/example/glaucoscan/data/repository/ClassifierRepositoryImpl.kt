package com.example.glaucoscan.data.repository

import android.graphics.Bitmap
import com.example.glaucoscan.core.commons.ImageClassifierHelper
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.repositories.ClassifierRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ClassifierRepositoryImpl @Inject constructor(
    private val helper: ImageClassifierHelper,
) : ClassifierRepository {
    override suspend fun classify(bitmap: Bitmap, model: ModelConfig): Result<ClassificationResult> =
        try {
            Result.success(helper.classify(bitmap, model))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}