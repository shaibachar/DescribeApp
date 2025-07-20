# DescribeApp

An Android app built with Jetpack Compose and Kotlin that uses AI to describe photos in both Hebrew and English.

## Features

- 📸 Take photos using device camera
- 🤖 AI-powered image description using DeepSeek API
- 🌍 Bilingual descriptions (Hebrew and English)
- 🇺🇸🇮🇱 Flag indicators for language support
- 💾 Save results with combined image and descriptions
- 📱 Modern Material 3 UI with Jetpack Compose
- 🔧 Permission handling for camera access

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Camera**: CameraX
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Architecture**: MVVM with StateFlow
- **Permissions**: Accompanist Permissions

## Setup Instructions

### Prerequisites

- Android Studio Flamingo or newer
- Android SDK 24+ (Android 7.0)
- DeepSeek API key

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/shaibachar/DescribeApp.git
   cd DescribeApp
   ```

2. Open the project in Android Studio

3. Get a DeepSeek API key:
   - Visit [DeepSeek API](https://platform.deepseek.com)
   - Create an account and get your API key

4. Configure the API key:
   - Open `app/src/main/java/com/example/describeapp/data/PhotoRepository.kt`
   - Replace `sk-your-deepseek-api-key-here` with your actual API key:
   ```kotlin
   private val apiKey = "sk-your-actual-api-key-here"
   ```

5. Build and run the project:
   ```bash
   ./gradlew assembleDebug
   ```

## Usage

1. Launch the app
2. Grant camera permission when prompted
3. Take a photo using the camera button
4. Wait for AI processing (requires internet connection)
5. View the results with Hebrew and English descriptions
6. Save the result or take another photo

## Project Structure

```
app/src/main/java/com/example/describeapp/
├── data/
│   ├── Models.kt           # Data models for API and app state
│   └── PhotoRepository.kt  # Repository for API calls
├── network/
│   ├── ApiClient.kt        # Retrofit configuration
│   └── DeepSeekApiService.kt # API service interface
├── ui/
│   ├── theme/              # Material 3 theming
│   ├── CameraScreen.kt     # Camera capture UI
│   ├── LoadingScreen.kt    # Processing state UI
│   ├── ResultScreen.kt     # Results display UI
│   └── MainViewModel.kt    # State management
├── utils/
│   ├── CameraUtils.kt      # Camera utilities
│   └── FileUtils.kt        # File operations
└── MainActivity.kt         # Main activity
```

## API Integration

The app integrates with DeepSeek's vision API to get image descriptions:

- Sends base64-encoded images to DeepSeek
- Makes two API calls: one for English, one for Hebrew
- Handles errors and loading states gracefully

## Permissions

The app requires the following permissions:
- `CAMERA` - To take photos
- `INTERNET` - To make API calls
- `READ_MEDIA_IMAGES` - To access saved images (Android 13+)
- `WRITE_EXTERNAL_STORAGE` - To save results (Android 10 and below)

## Languages Supported

- **English**: Primary language with US flag indicator
- **Hebrew**: Secondary language with Israel flag indicator
- RTL (Right-to-Left) layout support for Hebrew

## Security Note

⚠️ **Important**: This demo stores the API key in the source code for simplicity. In a production app, you should:
- Store API keys securely using Android Keystore
- Use environment variables or secure configuration
- Implement proper API key rotation
- Consider using a backend service to proxy API calls

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is for educational purposes. Please check DeepSeek's terms of service for API usage.
