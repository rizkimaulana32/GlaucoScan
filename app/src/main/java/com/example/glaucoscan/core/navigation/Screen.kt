package com.example.glaucoscan.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    object Home

    @Serializable
    object Gallery

    @Serializable
    object Camera
}