package com.example.pranksound_compose.component.prank_call

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView
) {
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var cameraProvider: ProcessCameraProvider? = null

    fun start(selector: CameraSelector) {
        cameraSelector = selector

        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            cameraProvider = future.get()

            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        }, ContextCompat.getMainExecutor(context))
    }

    fun stop() {
        cameraProvider?.unbindAll()
    }

    fun switchCamera() {
        cameraSelector =
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
                CameraSelector.DEFAULT_BACK_CAMERA
            else CameraSelector.DEFAULT_FRONT_CAMERA
    }

    fun currentSelector() = cameraSelector
}