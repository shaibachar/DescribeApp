package com.example.describeapp.data

data class PhotoDescription(
    val englishDescription: String,
    val hebrewDescription: String,
    val imageUri: String
)

data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: List<Content>
)

data class Content(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

data class ImageUrl(
    val url: String
)

data class DeepSeekResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageResponse
)

data class MessageResponse(
    val content: String
)