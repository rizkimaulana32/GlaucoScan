package com.example.glaucoscan.core.commons

import com.example.glaucoscan.domain.models.ModelConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelSelectionState @Inject constructor() {
    private val _selected = MutableStateFlow(ModelConfig.Default)
    val selected: StateFlow<ModelConfig> = _selected.asStateFlow()

    fun select(model: ModelConfig) {
        _selected.value = model
    }
}