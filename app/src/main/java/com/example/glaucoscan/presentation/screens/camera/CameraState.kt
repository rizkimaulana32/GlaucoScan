package com.example.glaucoscan.presentation.screens.camera

import androidx.annotation.StringRes
import com.example.glaucoscan.domain.models.ClassificationResult
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.presentation.screens.gallery.GalleryState

data class CameraState(
    val model: ModelConfig,
    val isLive: Boolean = true,
    val status: Status = Status.Initial,
) {
    sealed interface Status {
        data object Initial : Status
        data object NotFundus : Status
        data class Success(val result: ClassificationResult) : Status
        data class Error(@StringRes val message: Int) : Status
    }
}