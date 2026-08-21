package com.example.pranksoundalpha.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.plant.utils.minhtn.SharePreferenceExt
import com.example.pranksound.data.dto.prank.CustomSound
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.local.LocalData
import com.example.pranksoundalpha.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailSoundViewModel @Inject constructor(
    private val localData: LocalData
) : BaseViewModel() {

    private val _isLoop = MutableStateFlow(SharePreferenceExt.isLoopSave)
    val isLoop = _isLoop.asStateFlow()

    private val _isBg = MutableStateFlow(SharePreferenceExt.isBG)
    val isBg = _isBg.asStateFlow()

    private val _delay: MutableSharedFlow<Pair<Int, Boolean>> = MutableSharedFlow(replay = 1)
    val delay: SharedFlow<Pair<Int, Boolean>> = _delay

    init {
        viewModelScope.launch { _delay.emit(Pair(0, false)) }
    }

    fun toggleBG() {
        viewModelScope.launch {
            val newState = !_isBg.value
            _isBg.value = newState
            SharePreferenceExt.isBG = newState
        }
    }

    fun setBG(bg: Boolean) {
        _isBg.value = bg
        SharePreferenceExt.isBG = bg
    }

    fun setLoop() {
        viewModelScope.launch {
            val newState = !_isLoop.value
            _isLoop.value = newState
            Log.e("newState_loop", newState.toString())
            SharePreferenceExt.isLoopSave = newState
        }
    }

//    fun setLoop(loop: Boolean) {
//        _isLoop.value = loop
//        SharePreferenceExt.isLoopSave = loop
//    }

    fun getAllFavoriteSound(): Flow<List<FavoriteSound>> {
        return localData.getAllFavoriteSound()
    }

    fun updateFavoriteSound(sound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sound.isFavorite) {
                localData.deleteFavoriteSound(sound.name)
            } else {
                localData.insertFavoriteSound(
                    FavoriteSound(uniqueId = sound.name)
                )
            }
        }
    }

    fun updateFavoriteCustomSound(sound: CustomSound) {
        viewModelScope.launch(Dispatchers.IO) {
            if (sound.isFavorite) {
                localData.deleteFavoriteSound(sound.title)
            } else {
                localData.insertFavoriteSound(
                    FavoriteSound(uniqueId = sound.title, isCustom = true)
                )
            }
        }
    }


    fun updateSound(sound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
//            localData.updateSound(sound)
        }
    }

    fun setDelay(timeDelay: Int, isCustom: Boolean = false) {
        Log.d("MinhTN912 - LOGIC", "setDelay: $timeDelay, isCustom=$isCustom")
        viewModelScope.launch(Dispatchers.IO) {
            _delay.emit(Pair(timeDelay, isCustom))
        }

    }
}