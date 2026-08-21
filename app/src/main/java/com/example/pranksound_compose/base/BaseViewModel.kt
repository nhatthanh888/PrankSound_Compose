package com.example.pranksoundalpha.base

import androidx.lifecycle.ViewModel
import javax.inject.Inject
import com.example.pranksound.usecase.errors.ErrorManager

abstract class BaseViewModel : ViewModel() {
    @Inject
    lateinit var errorManager: ErrorManager
}
