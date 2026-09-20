package com.example.glaucoscan.core.di

import com.example.glaucoscan.data.repository.ClassifierRepositoryImpl
import com.example.glaucoscan.data.repository.ImageRepositoryImpl
import com.example.glaucoscan.domain.repositories.ClassifierRepository
import com.example.glaucoscan.domain.repositories.ImageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun classifier(impl: ClassifierRepositoryImpl): ClassifierRepository

    @Binds
    abstract fun images(impl: ImageRepositoryImpl): ImageRepository
}