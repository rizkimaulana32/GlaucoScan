package com.example.glaucoscan.domain.usecases

import android.net.Uri
import com.example.glaucoscan.domain.repositories.ClassifierRepository
import javax.inject.Inject

class LoadBitmapUseCase @Inject constructor(private val classifier: ClassifierRepository) {
    suspend operator fun invoke(uri: Uri) = classifier.loadImage(uri)
}