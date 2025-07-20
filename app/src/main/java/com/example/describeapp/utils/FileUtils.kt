package com.example.describeapp.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {
    
    fun saveResultImage(
        context: Context,
        originalImagePath: String,
        englishDescription: String,
        hebrewDescription: String
    ): File? {
        return try {
            val originalBitmap = BitmapFactory.decodeFile(originalImagePath)
            val width = originalBitmap.width
            val height = originalBitmap.height + 400 // Extra space for text
            
            val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)
            
            // Draw white background
            canvas.drawColor(Color.WHITE)
            
            // Draw original image
            canvas.drawBitmap(originalBitmap, 0f, 0f, null)
            
            // Draw text
            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 32f
                isAntiAlias = true
            }
            
            val textStartY = originalBitmap.height + 50f
            val textPadding = 20f
            
            // Draw English description
            drawMultilineText(canvas, "🇺🇸 $englishDescription", textPadding, textStartY, paint, width - 40)
            
            // Draw Hebrew description
            drawMultilineText(canvas, "🇮🇱 $hebrewDescription", textPadding, textStartY + 120f, paint, width - 40)
            
            // Save the result
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "described_image_$timeStamp.jpg"
            val file = File(context.getExternalFilesDir("Pictures"), fileName)
            
            FileOutputStream(file).use { out ->
                resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun drawMultilineText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint, maxWidth: Int) {
        val words = text.split(" ")
        var currentLine = ""
        var currentY = y
        
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val bounds = Rect()
            paint.getTextBounds(testLine, 0, testLine.length, bounds)
            
            if (bounds.width() > maxWidth && currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, currentY, paint)
                currentLine = word
                currentY += paint.textSize + 10
            } else {
                currentLine = testLine
            }
        }
        
        if (currentLine.isNotEmpty()) {
            canvas.drawText(currentLine, x, currentY, paint)
        }
    }
    
    fun shareImage(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}