package com.example.campuswaldo.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuswaldo.data.model.LeaderboardEntry
import com.example.campuswaldo.data.repository.WaldoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val entries: List<LeaderboardEntry> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: WaldoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getLeaderboard()
                .onSuccess { entries ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        entries = entries,
                        error = null
                    )
                }
                .onFailure { e ->
                    // Log the real error for debugging
                    Log.e("LeaderboardViewModel", "Failed to load leaderboard", e)

                    // Show a friendly message instead of "unexpected end of stream"
                    val friendlyMessage = "Could not load leaderboard. Please try again."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = friendlyMessage
                    )
                }
        }
    }
}