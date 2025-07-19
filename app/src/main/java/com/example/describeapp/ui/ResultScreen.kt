package com.example.describeapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.describeapp.R
import com.example.describeapp.data.PhotoDescription
import com.example.describeapp.utils.FileUtils
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    photoDescription: PhotoDescription,
    onTakeAnother: () -> Unit,
    onSaveResult: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Photo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            AsyncImage(
                model = photoDescription.imageUri,
                contentDescription = "Captured photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Descriptions with flags
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // English description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_flag_us),
                        contentDescription = "US Flag",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.english_description),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = photoDescription.englishDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Hebrew description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_flag_israel),
                        contentDescription = "Israel Flag",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.hebrew_description),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = photoDescription.hebrewDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onTakeAnother,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.retake_photo))
            }
            
            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        try {
                            val savedFile = FileUtils.saveResultImage(
                                context = context,
                                originalImagePath = photoDescription.imageUri,
                                englishDescription = photoDescription.englishDescription,
                                hebrewDescription = photoDescription.hebrewDescription
                            )
                            
                            if (savedFile != null) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.result_saved),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onSaveResult()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_saving),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_saving),
                                Toast.LENGTH_SHORT
                            ).show()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (isSaving) "Saving..." else stringResource(id = R.string.save_result)
                )
            }
        }
    }
}