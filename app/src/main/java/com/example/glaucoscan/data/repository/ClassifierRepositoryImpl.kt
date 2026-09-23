package com.example.glaucoscan.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.glaucoscan.core.commons.ImageClassifierHelper
import com.example.glaucoscan.core.commons.ImageLoaderHelper
import com.example.glaucoscan.core.commons.ValidatorHelper
import com.example.glaucoscan.domain.models.AnalysisOutcome
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.repositories.ClassifierRepository
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class ClassifierRepositoryImpl @Inject constructor(
    private val imageClassifier: ImageClassifierHelper,
    private val validator: ValidatorHelper,
    private val imageLoader: ImageLoaderHelper
) : ClassifierRepository {
    override suspend fun classify(bitmap: Bitmap, model: ModelConfig): Result<AnalysisOutcome> {
        if (!validator.isFundus(bitmap)) {
            return Result.success(AnalysisOutcome.NotFundus)
        }

        if (!validator.looksLikeFundus(bitmap)){
            return Result.success(AnalysisOutcome.NotFundus)
        }

        return try {
            Result.success(AnalysisOutcome.Detected(imageClassifier.classify(bitmap, model)))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadImage(uri: Uri): Result<Bitmap> = imageLoader.load(uri)
}