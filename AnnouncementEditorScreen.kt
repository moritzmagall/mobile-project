package com.ndejje.obituaryapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.ndejje.obituaryapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementEditorScreen(
    viewModel: AnnouncementViewModel,
    onNavigateToPreview: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateImage(it) }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            acquireCurrentLocation(context) { lat, lon ->
                viewModel.updateLocation(lat, lon)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(id = R.string.screen_editor)) }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (uiState.isValid) {
                        viewModel.saveCurrentAnnouncement()
                        onNavigateToPreview()
                    }
                },
                containerColor = if (uiState.isValid) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = stringResource(R.string.button_preview),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.deceasedName,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.label_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.dateOfBirth,
                onValueChange = viewModel::updateDob,
                label = { Text(stringResource(R.string.label_dob)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.dateOfDeath,
                onValueChange = viewModel::updateDod,
                label = { Text(stringResource(R.string.label_dod)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.burialDate,
                onValueChange = viewModel::updateBurialDate,
                label = { Text(stringResource(R.string.label_burial_date)) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.village,
                onValueChange = viewModel::updateVillage,
                label = { Text(stringResource(R.string.label_village)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.button_add_photo))
            }

            if (uiState.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uiState.imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .padding(4.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    val coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    
                    if (fineLocation == PackageManager.PERMISSION_GRANTED || coarseLocation == PackageManager.PERMISSION_GRANTED) {
                        acquireCurrentLocation(context) { lat, lon ->
                            viewModel.updateLocation(lat, lon)
                        }
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Acquire Burial Location")
            }

            if (uiState.latitude != null && uiState.longitude != null) {
                Text(text = "📍 Location set: ${uiState.latitude}, ${uiState.longitude}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
