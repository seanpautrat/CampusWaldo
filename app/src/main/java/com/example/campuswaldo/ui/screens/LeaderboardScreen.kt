package com.example.campuswaldo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.campuswaldo.data.model.LeaderboardEntry
import com.example.campuswaldo.ui.viewmodels.LeaderboardUiState
import com.example.campuswaldo.ui.viewmodels.LeaderboardViewModel

@Composable
fun LeaderboardRoute(
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LeaderboardScreen(uiState)
}

@Composable
fun LeaderboardScreen(uiState: LeaderboardUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.entries) { LeaderboardRow(it) }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Surface(
        tonalElevation = if (entry.rank <= 3) 4.dp else 1.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${entry.rank}. ${entry.username}")
            Text("${entry.points} pts")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    val preview = LeaderboardUiState(
        isLoading = false,
        entries = listOf(
            LeaderboardEntry(1, "sean23", 245),
            LeaderboardEntry(2, "hunter", 210)
        )
    )
    LeaderboardScreen(preview)
}