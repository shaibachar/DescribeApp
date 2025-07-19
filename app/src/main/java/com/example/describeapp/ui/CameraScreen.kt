package com.example.describeapp.ui

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.describeapp.utils.CameraUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(onImageCaptured: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    
    when {
        cameraPermissionState.status.isGranted -> {
            CameraContent(
                lifecycleOwner = lifecycleOwner,
                onImageCaptured = onImageCaptured
            )
        }
        cameraPermissionState.status.shouldShowRationale -> {
            PermissionRationale(onRequestPermission = { cameraPermissionState.launchPermissionRequest() })
        }
        else -> {
            PermissionDenied(onRequestPermission = { cameraPermissionState.launchPermissionRequest() })
        }
    }
}

@Composable
private fun CameraContent(
    lifecycleOwner: LifecycleOwner,
    onImageCaptured: (String) -> Unit
) {
    val context = LocalContext.current
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { preview ->
                    previewView = preview
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        
                        val previewUseCase = Preview.Builder().build().also {
                            it.setSurfaceProvider(preview.surfaceProvider)
                        }
                        
                        val imageCaptureUseCase = ImageCapture.Builder().build()
                        imageCapture = imageCaptureUseCase
                        
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                previewUseCase,
                                imageCaptureUseCase
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Capture button
        FloatingActionButton(
            onClick = {
                imageCapture?.let { capture ->
                    val imageFile = CameraUtils.createImageFile(context)
                    capture.takePicture(
                        ImageCapture.OutputFileOptions.Builder(imageFile).build(),
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                // Compress the image before processing
                                val compressedFile = CameraUtils.compressImage(imageFile)
                                onImageCaptured(compressedFile.absolutePath)
                            }
                            
                            override fun onError(exception: ImageCapture.ImageCaptureException) {
                                exception.printStackTrace()
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Take Photo",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun PermissionRationale(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is required to take photos",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Grant Permission")
        }
    }
}

@Composable
private fun PermissionDenied(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission is required to use this app",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRequestPermission) {
            Text("Request Permission")
        }
    }
}