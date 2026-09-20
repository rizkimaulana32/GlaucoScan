package com.example.glaucoscan.presentation.screens.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glaucoscan.R
import com.example.glaucoscan.core.commons.ModelSelectionState
import com.example.glaucoscan.domain.models.ModelConfig
import com.example.glaucoscan.domain.usecases.ClassifyBitmapUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val classify: ClassifyBitmapUseCase,
    private val modelSelection: ModelSelectionState,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraState(model = modelSelection.selected.value))
    val uiState: StateFlow<CameraState> = _uiState.asStateFlow()

    private val busy = AtomicBoolean(false)

    init {
        modelSelection.selected.onEach { m -> _uiState.update { it.copy(model = m) } }
            .launchIn(viewModelScope)
    }

    fun onFrame(capture: () -> Bitmap) {
        if (!_uiState.value.isLive || !busy.compareAndSet(false, true)) return
        val bitmap = runCatching(capture).getOrElse { busy.set(false); return }

        viewModelScope.launch {
            val outcome = try {
                classify(bitmap, modelSelection.selected.value)
            } finally {
                bitmap.recycle()
            }
            val next = outcome.fold(
                onSuccess = { CameraState.Status.Success(it) },
                onFailure = {
                    Log.e("Camera", "Inference failed", it)
                    CameraState.Status.Error(R.string.error_inference) },
            )
            _uiState.update { it.copy(status = next) }
            delay(FRAME_INTERVAL_MS)
            busy.set(false)
        }
    }

    fun onToggleLive() = _uiState.update { it.copy(isLive = !it.isLive) }

    fun onModelSelected(model: ModelConfig) = modelSelection.select(model)

    private companion object { const val FRAME_INTERVAL_MS = 350L }
}