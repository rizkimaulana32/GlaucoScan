package com.example.glaucoscan.domain.repositories

import android.graphics.Bitmap
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.ModelConfig

interface ClassifierRepository {
    suspend fun classify(bitmap: Bitmap, model: ModelConfig): Result<ClassificationResult>
}