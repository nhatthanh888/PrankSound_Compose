package com.example.pranksoundalpha.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.pranksound.data.DataRepository
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksound_compose.component.prank_call.CallUiState
import com.example.pranksoundalpha.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrankCallViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : BaseViewModel() {
    val isCameraOn = MutableStateFlow(true)
    val isMicOn = MutableStateFlow(true)
    val isSpeakerOn = MutableStateFlow(true)

    private val _prankCallList = MutableStateFlow<List<PrankCall>>(emptyList())
    val prankCallList = _prankCallList.asStateFlow()

    private val _prankCall = MutableStateFlow<PrankCall?>(null)
    val prankCall = _prankCall.asStateFlow()

    private val _uiState = MutableStateFlow(CallUiState.INCOMING)
    val uiState = _uiState.asStateFlow()

    private val _callDuration = MutableStateFlow<Long?>(null)
    val callDuration = _callDuration.asStateFlow()

    fun endCall(duration: Long?) {
        _callDuration.value = duration
    }

    fun clearCallDuration() {
        _callDuration.value = null
    }


    fun getPrankCall() {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.getPrankCall().collect {
                _prankCallList.emit(it)
            }
        }
    }

    fun setStateUI(state: CallUiState) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.emit(state)
        }
    }

    fun selectContact(contact: PrankCall) {
        viewModelScope.launch(Dispatchers.IO) {
            _prankCall.emit(contact)
        }

    }
}