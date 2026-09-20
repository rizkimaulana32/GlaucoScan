package com.example.glaucoscan.domain.models

enum class GlaucomaLabel { GLAUCOMA, NORMAL }

data class ClassificationResult(
    val glaucomaProbability: Float,
    val label: GlaucomaLabel,
    val latencyMs: Float,
    val inputPrecision: String,
    val model: ModelConfig,
)