package com.example.glaucoscan.domain.repositories

import android.graphics.Bitmap
import android.net.Uri

interface ImageRepository {
    suspend fun load(uri: Uri): Result<Bitmap>
}
