package com.example.describeapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.describeapp.data.PhotoDescription
import com.example.describeapp.data.PhotoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Success(val photoDescription: PhotoDescription) : UiState()
    data class Error(val message: String) : UiState()
}

class MainViewModel : ViewModel() {
    private val repository = PhotoRepository()
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun processPhoto(imageFile: File) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            repository.getPhotoDescription(imageFile).fold(
                onSuccess = { photoDescription ->
                    _uiState.value = UiState.Success(photoDescription)
                },
                onFailure = { exception ->
                    _uiState.value = UiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }
    
    fun resetState() {
        _uiState.value = UiState.Initial
    }
}