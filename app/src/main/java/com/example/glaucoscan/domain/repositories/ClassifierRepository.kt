package com.example.glaucoscan.domain.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.example.glaucoscan.domain.models.AnalysisOutcome
import com.example.glaucoscan.domain.models.ModelConfig

interface ClassifierRepository {
    suspend fun classify(bitmap: Bitmap, model: ModelConfig): Result<AnalysisOutcome>
    suspend fun loadImage(uri: Uri): Result<Bitmap>
}