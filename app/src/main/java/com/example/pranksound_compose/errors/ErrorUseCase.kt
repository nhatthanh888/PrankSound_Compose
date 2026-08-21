package com.example.pranksound.usecase.errors

import com.example.pranksound.data.error.Error

interface ErrorUseCase {
    fun getError(errorCode: Int): Error
}
