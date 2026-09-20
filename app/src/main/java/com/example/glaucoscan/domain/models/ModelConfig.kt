package com.example.glaucoscan.domain.models

enum class Dataset(val label: String, val fileKey: String) {
    ACRIMA("ACRIMA", "acrima"),
    DRISHTI("DRISHTI-GS", "drishti"),
}

enum class Variant(val label: String, val descriptor: String, val fileKey: String) {
    BASELINE("Baseline", "Model penuh · referensi", "baseline"),
    SP_50("SP 50%", "Structured pruning · 50%", "sp50"),
    SP_75("SP 75%", "Structured pruning · 75%", "sp75"),
    QAT_INT8("QAT INT8", "Quantization-aware · 8-bit", "int8"),
    QAT_INT4("QAT INT4", "Quantization-aware · 4-bit", "int4"),
}

data class ModelConfig(val dataset: Dataset, val variant: Variant) {
    val assetPath: String get() = "models/${dataset.fileKey}_${variant.fileKey}.onnx"
    val displayName: String get() = "${dataset.label} · ${variant.label}"

    companion object {
        val Default = ModelConfig(Dataset.ACRIMA, Variant.BASELINE)
    }
}