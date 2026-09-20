package com.example.glaucoscan.domain.usecases

import android.graphics.Bitmap
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.repositories.ClassifierRepository
import javax.inject.Inject

class ClassifyBitmapUseCase @Inject constructor(
    private val classifier: ClassifierRepository,
) {
    suspend operator fun invoke(bitmap: Bitmap, modelConfig: ModelConfig) =
        classifier.classify(bitmap, modelConfig)
}