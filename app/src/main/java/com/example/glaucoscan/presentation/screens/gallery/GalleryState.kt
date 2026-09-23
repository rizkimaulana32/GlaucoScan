package com.example.glaucoscan.presentation.screens.gallery

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.ImageBitmap
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.ModelConfig

data class GalleryState(
    val model: ModelConfig,
    val preview: ImageBitmap? = null,
    val status: Status = Status.Initial,
) {
    sealed interface Status {
        data object Initial : Status
        data object Loading : Status
        data object NotFundus : Status
        data class Success(val result: ClassificationResult) : Status
        data class Error(@StringRes val message: Int) : Status
    }
}