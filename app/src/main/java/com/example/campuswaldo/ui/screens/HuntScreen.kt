package com.example.campuswaldo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campuswaldo.data.model.RedeemResult
import com.example.campuswaldo.data.model.WaldoOfDay
import com.example.campuswaldo.ui.viewmodels.HuntUiState
import com.example.campuswaldo.ui.viewmodels.HuntViewModel
import com.example.campuswaldo.ui.viewmodels.UserViewModel 

@Composable
fun HuntRoute(
    viewModel: HuntViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()   
) {
    val uiState by viewModel.uiState.collectAsState()
    val time by userViewModel.timeRemaining.collectAsState()   

    HuntScreen(
        uiState = uiState,
        onCodeChange = viewModel::updateCodeInput,
        onRedeemClick = viewModel::redeemCode,
        timeRemaining = time         
    )
}

@Composable
fun HuntScreen(
    uiState: HuntUiState,
    onCodeChange: (String) -> Unit,
    onRedeemClick: () -> Unit,
    timeRemaining: String                     
) {
    val waldo = uiState.waldo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Where's Waldo? Campus Edition", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(12.dp))

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            waldo == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Waldo found for today.")
                }
            }

            else -> {
                // --- WALDO header ----
                WaldoHeader(waldo)

                Spacer(Modifier.height(8.dp))

                // --- TIMER SECTION ---
                Text(
                    text = "Time left today: $timeRemaining",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(6.dp)
                )

                Spacer(Modifier.height(12.dp))

                // ----- Hints -----
                Text("Live hints", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))

                HintRow(waldo.hints)

                Spacer(Modifier.height(24.dp))

                // ----- CODE INPUT -----
                CodeSection(
                    codeInput = uiState.codeInput,
                    onCodeChanged = onCodeChange,
                    onRedeemClicked = onRedeemClick
                )

                Spacer(Modifier.height(16.dp))

                // ----- Redeem Result -----
                uiState.redeemResult?.let { result ->
                    Text(
                        text = "${result.message} ${
                            if (result.pointsEarned > 0) "(+${result.pointsEarned} pts)" else ""
                        }",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = if (result.correct)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun WaldoHeader(waldo: WaldoOfDay) {
    Text("Today's Waldo: ${waldo.alias}", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Waldo photo placeholder")
    }
}

@Composable
private fun HintRow(hints: List<String>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(hints) { HintChip(it) }
    }
}

@Composable
private fun HintChip(hint: String) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = hint,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun CodeSection(
    codeInput: String,
    onCodeChanged: (String) -> Unit,
    onRedeemClicked: () -> Unit
) {
    OutlinedTextField(
        value = codeInput,
        onValueChange = onCodeChanged,
        label = { Text("Enter today's secret code") },
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = onRedeemClicked
        ) {
            Text("Redeem")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HuntScreenPreview() {
    val preview = HuntUiState(
        isLoading = false,
        waldo = WaldoOfDay(
            alias = "Library Waldo",
            imageUrl = "",
            hints = listOf("Quiet", "Books", "Need ID")
        ),
        codeInput = "",
        redeemResult = RedeemResult(true, 18, "You found today’s Waldo!")
    )

    HuntScreen(
        uiState = preview,
        onCodeChange = {},
        onRedeemClick = {},
        timeRemaining = "02:13:45"     // Preview timer
    )
}
