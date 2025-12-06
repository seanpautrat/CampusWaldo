package com.example.campuswaldo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campuswaldo.ui.viewmodels.WaldoOnlyViewModel

@Composable
fun WaldoOnlyScreen(
    viewModel: WaldoOnlyViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var hintText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Waldo Control Panel", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        Text("Post a hint:", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = hintText,
            onValueChange = { hintText = it },
            label = { Text("Hint text") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { hintText = "" },
            enabled = hintText.isNotBlank()
        ) {
            Text("Post hint")
        }

        Spacer(Modifier.height(24.dp))

        when {
            uiState.isLoading -> {
                Text("Loading code…", style = MaterialTheme.typography.bodyLarge)
            }
            uiState.error != null -> {
                Text(
                    "Error loading code",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                Text(
                    "Today’s code: ${uiState.secretCode}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}