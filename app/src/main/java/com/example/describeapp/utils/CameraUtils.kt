package com.example.describeapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CameraUtils {
    
    fun createImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir("Pictures")
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }
    
    fun capturePhoto(
        imageCapture: ImageCapture,
        outputFile: File,
        onImageCaptured: (File) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(outputFile.parentFile?.let { 
                // Get context from file's parent directory
                null
            } ?: run { 
                // Fallback to creating the options without specific executor
                outputOptions
                return
            }),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    onImageCaptured(outputFile)
                }
                
                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            }
        )
    }
    
    fun compressImage(file: File, maxWidth: Int = 1024, maxHeight: Int = 1024): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val ratio = Math.min(
            maxWidth.toFloat() / bitmap.width,
            maxHeight.toFloat() / bitmap.height
        )
        
        val width = (ratio * bitmap.width).toInt()
        val height = (ratio * bitmap.height).toInt()
        
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
        
        FileOutputStream(file).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }
        
        return file
    }
}