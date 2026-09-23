package com.example.glaucoscan.presentation.screens.gallery

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glaucoscan.R
import com.example.glaucoscan.core.commons.ModelSelectionState
import com.example.glaucoscan.domain.models.AnalysisOutcome
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.usecases.ClassifyBitmapUseCase
import com.example.glaucoscan.domain.usecases.LoadBitmapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val loadBitmap: LoadBitmapUseCase,
    private val classify: ClassifyBitmapUseCase,
    private val modelSelection: ModelSelectionState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(GalleryState(model = modelSelection.selected.value))
    val uiState: StateFlow<GalleryState> = _uiState.asStateFlow()

    private var bitmap: Bitmap? = null
    private var job: Job? = null

    init {
        modelSelection.selected.onEach { model ->
            _uiState.update { it.copy(model = model) }
            bitmap?.let { bmp -> launchAnalysis { infer(bmp) } }
        }.launchIn(viewModelScope)
    }

    private fun launchAnalysis(block: suspend () -> GalleryState.Status) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.update { it.copy(status = GalleryState.Status.Loading) }
            val outcome = block()
            _uiState.update { it.copy(status = outcome) }
        }
    }

    private suspend fun infer(bmp: Bitmap): GalleryState.Status =
        classify(bmp, modelSelection.selected.value).fold(
            onSuccess = { outcome ->
                when (outcome) {
                    is AnalysisOutcome.Detected -> GalleryState.Status.Success(outcome.result)
                    AnalysisOutcome.NotFundus -> GalleryState.Status.NotFundus
                }
            },
            onFailure = {
                Log.e("Gallery", "Inference failed", it)
                GalleryState.Status.Error(R.string.error_inference)
            },
        )

    fun onImagePicked(uri: Uri) = launchAnalysis {
        val bmp = loadBitmap(uri).getOrElse {
            return@launchAnalysis GalleryState.Status.Error(R.string.error_open_image)
        }
        bitmap = bmp
        _uiState.update { it.copy(preview = bmp.asImageBitmap()) }
        infer(bmp)
    }

    fun onModelSelected(model: ModelConfig) = modelSelection.select(model)
}