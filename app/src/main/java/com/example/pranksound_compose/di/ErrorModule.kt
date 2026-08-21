package com.example.pranksound.di

import com.example.pranksound.data.error.mapper.ErrorMapper
import com.example.pranksound.data.error.mapper.ErrorMapperSource
import com.example.pranksound.usecase.errors.ErrorUseCase
import com.example.pranksound.usecase.errors.ErrorManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// with @Module we Telling Dagger that, this is a Dagger module
@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {
    @Binds
    @Singleton
    abstract fun provideErrorFactoryImpl(errorManager: ErrorManager): ErrorUseCase

    @Binds
    @Singleton
    abstract fun provideErrorMapper(errorMapper: ErrorMapper): ErrorMapperSource
}
