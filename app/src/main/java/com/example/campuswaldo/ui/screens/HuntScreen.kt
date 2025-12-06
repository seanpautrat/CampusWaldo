package com.example.campuswaldo.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campuswaldo.data.model.RedeemResult
import com.example.campuswaldo.data.model.WaldoOfDay
import com.example.campuswaldo.ui.viewmodels.HuntUiState
import com.example.campuswaldo.ui.viewmodels.HuntViewModel
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun HuntRoute(
    viewModel: HuntViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeRemaining by rememberCountdownToMidnight()

    HuntScreen(
        uiState = uiState,
        onCodeChange = viewModel::updateCodeInput,
        onRedeemClick = viewModel::redeemCode,
        timeRemaining = timeRemaining
    )
}

/**
 * Returns a State<String> that updates every second with the time left until midnight.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun rememberCountdownToMidnight(): State<String> {
    val timeState = remember { mutableStateOf("00:00:00") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            val midnight = now.toLocalDate().plusDays(1).atStartOfDay()
            val duration = Duration.between(now, midnight)
            val totalSeconds = duration.seconds.coerceAtLeast(0)

            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            timeState.value = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            delay(1_000L)
        }
    }

    return timeState
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
                WaldoHeader(waldo)

                Spacer(Modifier.height(8.dp))

                // --- TIMER SECTION ---
                Text(
                    text = "Time left today: $timeRemaining",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(6.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text("Live hints", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))

                HintRow(waldo.hints)

                Spacer(Modifier.height(24.dp))

                CodeSection(
                    codeInput = uiState.codeInput,
                    onCodeChanged = onCodeChange,
                    onRedeemClicked = onRedeemClick
                )

                Spacer(Modifier.height(16.dp))

                uiState.redeemResult?.let { result ->
                    Text(
                        text = "${result.message} " +
                                if (result.pointsEarned > 0) "(+${result.pointsEarned} pts)" else "",
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
        Button(onClick = onRedeemClicked) {
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
        timeRemaining = "02:13:45"
    )
}