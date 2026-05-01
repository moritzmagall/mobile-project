package com.ndejje.obituaryapp.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ndejje.obituaryapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewShareScreen(
    viewModel: AnnouncementViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val condolenceMessage = if (uiState.selectedTemplateId == 0) {
        stringResource(R.string.template_classic_msg)
    } else {
        stringResource(R.string.template_buganda_msg)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.screen_preview)) }) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { shareAnnouncement(context, uiState, condolenceMessage) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.button_share))
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.button_save))
                }
            }
        }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(uiState.imageUri),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(text = uiState.deceasedName, style = MaterialTheme.typography.headlineLarge)
                Text(
                    text = "Born: ${uiState.dateOfBirth} | Died: ${uiState.dateOfDeath}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Burial: ${uiState.burialDate} | Village: ${uiState.village}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (uiState.latitude != null && uiState.longitude != null) {
                    Text(
                        text = "📍 Burial location: ${uiState.latitude}, ${uiState.longitude}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = condolenceMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

private fun shareAnnouncement(context: Context, state: EditorUiState, message: String) {
    val shareText = """
 REST IN PEACE 
${state.deceasedName}
Born: ${state.dateOfBirth} | Died: ${state.dateOfDeath}
Burial: ${state.burialDate}
Location: ${state.village}
${if (state.latitude != null) "Map: https://maps.google.com/?q=${state.latitude},${state.longitude}" else ""}

$message

-- ObituaryApp
""".trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share announcement via"))
}
