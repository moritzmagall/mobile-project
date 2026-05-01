package com.ndejje.obituaryapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ndejje.obituaryapp.R
import com.ndejje.obituaryapp.ui.theme.APlusBlue
import com.ndejje.obituaryapp.ui.theme.White

data class Template(val id: Int, val nameRes: Int, val messageRes: Int, val backgroundColor: androidx.compose.ui.graphics.Color)

val templates = listOf(
    Template(0, R.string.template_classic, R.string.template_classic_msg, White),
    Template(1, R.string.template_buganda, R.string.template_buganda_msg, APlusBlue.copy(alpha = 0.1f))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateGalleryScreen(
    onTemplateSelected: (Int) -> Unit,
    onNavigateToEditor: () -> Unit
) {
    var selectedId by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            Box { TopAppBar(title = { Text(stringResource(R.string.screen_templates)) }) }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                onTemplateSelected(selectedId)
                onNavigateToEditor()
            }) {
                Text(stringResource(R.string.button_next))
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(templates) { template ->
                TemplateCard(
                    template = template,
                    isSelected = selectedId == template.id,
                    onClick = { selectedId = template.id }
                )
            }
        }
    }
}

@Composable
fun TemplateCard(template: Template, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = template.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 4.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(template.nameRes), style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(template.messageRes), style = MaterialTheme.typography.bodyMedium)
        }
    }
}