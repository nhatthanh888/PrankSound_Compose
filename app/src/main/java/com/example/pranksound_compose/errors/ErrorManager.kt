package com.example.pranksound.usecase.errors

import com.example.pranksound.data.error.Error
import com.example.pranksound.data.error.mapper.ErrorMapper
import javax.inject.Inject

/**
 * Created by TruyenIT
 */

class ErrorManager @Inject constructor(private val errorMapper: ErrorMapper) : ErrorUseCase {
    override fun getError(errorCode: Int): Error {
        return Error(code = errorCode, description = errorMapper.errorsMap.getValue(errorCode))
    }
}
