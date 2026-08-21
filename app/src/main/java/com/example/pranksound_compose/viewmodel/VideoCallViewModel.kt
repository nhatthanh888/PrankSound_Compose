package com.example.pranksound_compose.viewmodel

import android.app.Application
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.pranksound.data.dto.prank.PrankCall
import com.example.pranksoundalpha.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class VideoCallViewModel @Inject constructor(
    private val app: Application
) : BaseViewModel() {

    // ===== STATE =====
    val prankCall = MutableStateFlow<PrankCall?>(null)

    val isCameraOn = MutableStateFlow(true)
    val isMicOn = MutableStateFlow(true)
    val isSpeakerOn = MutableStateFlow(true)

    var startTime = 0L
        private set

    // ===== EXOPLAYER =====
    var exoPlayer: ExoPlayer? = null
        private set

    fun initCall(context: Context, call: PrankCall) {
        prankCall.value = call
        startTime = System.currentTimeMillis()

        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(call.videoLink))
                prepare()
                playWhenReady = true
            }
        }
    }

    fun toggleCamera() {
        isCameraOn.value = !isCameraOn.value
    }

    fun toggleMic() {
        isMicOn.value = !isMicOn.value
    }

    fun toggleSpeaker() {
        isSpeakerOn.value = !isSpeakerOn.value
        exoPlayer?.volume = if (isSpeakerOn.value) 1f else 0f
    }

    fun endCall(): Long {
        val duration = (System.currentTimeMillis() - startTime) / 1000
        release()
        return duration
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onCleared() {
        release()
        super.onCleared()
    }
}