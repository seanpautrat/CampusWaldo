package com.example.campuswaldo.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.campuswaldo.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

enum class UserRole {
    WALDO,
    HUNTER
}

data class AppUiState(
    val user: User,
    val role: UserRole
)

class UserViewModel : ViewModel() {

    // Change isTodayWaldo to true to preview the Waldo-only flow
    private val initialUser = User(
        username = "sean",
        isTodayWaldo = false
    )

    private val _appUiState = MutableStateFlow(
        AppUiState(
            user = initialUser,
            role = if (initialUser.isTodayWaldo) UserRole.WALDO else UserRole.HUNTER
        )
    )
    val appUiState = _appUiState.asStateFlow()

        // -------------------------
    // Timer state
    // -------------------------
    var timeRemaining: String = "00:00:00"
        private set

    private var targetTime: Long = 0L

    init {
        initializeDailyTimer()
    }

    // -------------------------
    // Timer logic
    // -------------------------

    /** Starts a 24-hour countdown ending at next midnight */
    fun initializeDailyTimer() {
        val tomorrow = LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())

        targetTime = tomorrow.toInstant().toEpochMilli()

        startTimer()
    }

    /** Coroutine ticking every second */
    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                val now = System.currentTimeMillis()
                val diff = targetTime - now

                if (diff <= 0) {
                    timeRemaining = "00:00:00"
                    break
                }

                timeRemaining = formatTime(diff)
                delay(1000)
            }
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}

