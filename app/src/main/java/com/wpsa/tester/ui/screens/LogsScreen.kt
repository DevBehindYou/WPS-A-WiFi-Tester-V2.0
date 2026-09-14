package com.wpsa.tester.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.core.logging.LogEntry
import com.wpsa.tester.core.logging.LogLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogsScreen(
    appLogger: AppLogger
) {
    val context = LocalContext.current
    val allLogs by appLogger.logs.collectAsState()

    var selectedLevel by remember { mutableStateOf<LogLevel?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }

    val filteredLogs = remember(allLogs, selectedLevel, searchQuery) {
        allLogs.filter { entry ->
            val matchesLevel = selectedLevel == null || entry.level == selectedLevel
            val matchesQuery = searchQuery.isBlank() ||
                    entry.component.contains(searchQuery, ignoreCase = true) ||
                    entry.operation.contains(searchQuery, ignoreCase = true) ||
                    entry.result.contains(searchQuery, ignoreCase = true)
            matchesLevel && matchesQuery
        }
    }

    val listState = rememberLazyListState()

    // Auto-scroll to latest log entry
    LaunchedEffect(filteredLogs.size) {
        if (filteredLogs.isNotEmpty()) {
            listState.animateScrollToItem(filteredLogs.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("System & Root Logs", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(Icons.Default.Search, contentDescription = "Search logs")
                    }
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("WPS App Logs", appLogger.exportLogsText())
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Logs copied to clipboard", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy logs")
                    }
                    IconButton(onClick = { appLogger.clearLogs() }) {
                        Icon(Icons.Default.ClearAll, contentDescription = "Clear logs")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Animated search bar
            AnimatedVisibility(visible = showSearchBar) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter by keyword, component...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )
            }

            // Severity filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLevel == null,
                    onClick = { selectedLevel = null },
                    label = { Text("ALL (${allLogs.size})") }
                )
                FilterChip(
                    selected = selectedLevel == LogLevel.INFO,
                    onClick = { selectedLevel = if (selectedLevel == LogLevel.INFO) null else LogLevel.INFO },
                    label = { Text("INFO") }
                )
                FilterChip(
                    selected = selectedLevel == LogLevel.WARN,
                    onClick = { selectedLevel = if (selectedLevel == LogLevel.WARN) null else LogLevel.WARN },
                    label = { Text("WARN") }
                )
                FilterChip(
                    selected = selectedLevel == LogLevel.ERROR,
                    onClick = { selectedLevel = if (selectedLevel == LogLevel.ERROR) null else LogLevel.ERROR },
                    label = { Text("ERROR") }
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No logs recorded yet. Shell commands, network events, and diagnostics will appear here in real-time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLogs, key = { it.id }) { entry ->
                        LogItemCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
fun LogItemCard(entry: LogEntry) {
    val (badgeBg, badgeFg) = when (entry.level) {
        LogLevel.INFO -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        LogLevel.WARN -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        LogLevel.ERROR -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        LogLevel.DEBUG -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(badgeBg, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entry.level.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeFg,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${entry.component} • ${entry.operation}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = entry.formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.result,
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (entry.durationMs != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Duration: ${entry.durationMs}ms",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
