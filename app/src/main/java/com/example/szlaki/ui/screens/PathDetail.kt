package com.example.szlaki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.szlaki.model.Path
import com.example.szlaki.model.SavedTime
import com.example.szlaki.ui.components.AnimatedLogo
import com.example.szlaki.ui.components.Stopwatch
import com.example.szlaki.ui.components.formatTime
import com.example.szlaki.ui.theme.LOGO_PATH_DATA
import com.example.szlaki.viewmodel.PathViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PathDetail(path: Path, onBack: () -> Unit, viewModel: PathViewModel = viewModel()) {
    var longDescription by remember { mutableStateOf<String?>(null) }
    var isLoadingDescription by remember { mutableStateOf(true) }
    var showSaveDialog by remember { mutableStateOf(false) }
    
    val savedTimes by viewModel.getSavedTimesForPath(path.id).collectAsState(initial = emptyList())

    LaunchedEffect(path.parkCode) {
        isLoadingDescription = true
        viewModel.loadPathDescription(path) { description ->
            longDescription = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().trim()
            isLoadingDescription = false
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Zapisz czas") },
            text = { Text("Czy chcesz zakończyć i zapisać trasę?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.saveCurrentTime(path.id, path.timeMillis)
                        viewModel.resetTimer(path)
                        showSaveDialog = false
                    }
                ) { Text("Tak") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Nie") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = path.imageUrl,
            contentDescription = path.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = path.name, style = MaterialTheme.typography.headlineLarge)
            
            Spacer(modifier = Modifier.height(16.dp))

            Stopwatch(
                timeMillis = path.timeMillis,
                isRunning = path.isTimerRunning,
                isAnyOtherTimerRunning = viewModel.isAnyTimerRunning && !path.isTimerRunning,
                onToggle = { viewModel.toggleTimer(path) },
                onReset = { viewModel.resetTimer(path) },
                onSave = { showSaveDialog = true },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (savedTimes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                RankingSection(
                    savedTimes = savedTimes,
                    onDelete = { viewModel.deleteSavedTime(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoadingDescription) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedLogo(
                        modifier = Modifier.size(100.dp, 25.dp),
                        pathData = LOGO_PATH_DATA,
                        animationDuration = 1500,
                        strokeWidth = 2f
                    )
                }
            } else {
                Text(
                    text = longDescription ?: "Brak dostępnego opisu.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Powrót do listy")
            }
        }
    }
}

@Composable
fun RankingSection(savedTimes: List<SavedTime>, onDelete: (SavedTime) -> Unit) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Twoje Najlepsze Czasy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        savedTimes.take(5).forEachIndexed { index, savedTime ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Column {
                        Text(
                            text = formatTime(savedTime.timeMillis),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = dateFormat.format(Date(savedTime.timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = { onDelete(savedTime) }, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Usuń",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (index < savedTimes.size - 1 && index < 4) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}
