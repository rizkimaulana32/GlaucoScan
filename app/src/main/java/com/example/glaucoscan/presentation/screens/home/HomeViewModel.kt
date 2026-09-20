package com.example.glaucoscan.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.example.glaucoscan.core.commons.ModelSelectionState
import com.example.glaucoscan.domain.models.ModelConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val modelSelection: ModelSelectionState
) : ViewModel() {

    val uiState: StateFlow<ModelConfig> = modelSelection.selected

    fun onModelSelected(model: ModelConfig) = modelSelection.select(model)
}