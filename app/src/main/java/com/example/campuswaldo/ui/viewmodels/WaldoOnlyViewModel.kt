package com.example.campuswaldo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuswaldo.data.repository.WaldoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WaldoOnlyUiState(
    val secretCode: String = "",
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class WaldoOnlyViewModel @Inject constructor(
    private val repository: WaldoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaldoOnlyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSecretCode()
    }

    private fun loadSecretCode() {
        viewModelScope.launch {
            repository.getSecretCode()
                .onSuccess { code ->
                    _uiState.value = WaldoOnlyUiState(
                        secretCode = code,
                        isLoading = false
                    )
                }
                .onFailure { e ->
                    _uiState.value = WaldoOnlyUiState(
                        secretCode = "",
                        isLoading = false,
                        error = e.message ?: "Failed to load code"
                    )
                }
        }
    }
}