package com.example.glaucoscan.domain.models

import com.example.glaucoscan.R

enum class Dataset(val label: String, val fileKey: String, val descriptionRes: Int) {
    ACRIMA("ACRIMA", "acrima", R.string.dataset_desc_acrima),
    DRISHTI("DRISHTI-GS", "drishti", R.string.dataset_desc_drishti),
}

data class ModelConfig(val dataset: Dataset) {
    val assetPath: String get() = "models/${dataset.fileKey}_sp75.onnx"
    val displayName: String get() = "${dataset.label}"

    companion object {
        val Default = ModelConfig(Dataset.ACRIMA)
    }
}