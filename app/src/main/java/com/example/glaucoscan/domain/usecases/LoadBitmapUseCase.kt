package com.example.glaucoscan.domain.usecases

import android.net.Uri
import com.example.glaucoscan.domain.repositories.ImageRepository
import javax.inject.Inject

class LoadBitmapUseCase @Inject constructor(private val images: ImageRepository) {
    suspend operator fun invoke(uri: Uri) = images.load(uri)
}