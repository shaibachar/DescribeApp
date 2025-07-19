package com.example.describeapp.data

import android.util.Base64
import com.example.describeapp.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class PhotoRepository {
    private val apiService = ApiClient.deepSeekApiService
    
    // Note: In a real app, this should be stored securely (not hardcoded)
    private val apiKey = "YOUR_DEEPSEEK_API_KEY" // Replace with actual API key
    
    suspend fun getPhotoDescription(imageFile: File): Result<PhotoDescription> = withContext(Dispatchers.IO) {
        try {
            val imageBytes = imageFile.readBytes()
            val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
            val imageUrl = "data:image/jpeg;base64,$base64Image"
            
            // Create request for English description
            val englishRequest = DeepSeekRequest(
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            Content(type = "text", text = "Describe this image in one sentence in English."),
                            Content(type = "image_url", image_url = ImageUrl(imageUrl))
                        )
                    )
                )
            )
            
            // Create request for Hebrew description
            val hebrewRequest = DeepSeekRequest(
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            Content(type = "text", text = "תאר את התמונה הזו במשפט אחד בעברית."),
                            Content(type = "image_url", image_url = ImageUrl(imageUrl))
                        )
                    )
                )
            )
            
            // Make API calls
            val englishResponse = apiService.getImageDescription("Bearer $apiKey", englishRequest)
            val hebrewResponse = apiService.getImageDescription("Bearer $apiKey", hebrewRequest)
            
            if (englishResponse.isSuccessful && hebrewResponse.isSuccessful) {
                val englishDescription = englishResponse.body()?.choices?.firstOrNull()?.message?.content ?: "No description available"
                val hebrewDescription = hebrewResponse.body()?.choices?.firstOrNull()?.message?.content ?: "אין תיאור זמין"
                
                Result.success(
                    PhotoDescription(
                        englishDescription = englishDescription,
                        hebrewDescription = hebrewDescription,
                        imageUri = imageFile.absolutePath
                    )
                )
            } else {
                Result.failure(Exception("API call failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}