package com.example.glaucoscan.domain.models

sealed interface AnalysisOutcome {
    data class Detected(val result: ClassificationResult) : AnalysisOutcome
    data object NotFundus : AnalysisOutcome
}