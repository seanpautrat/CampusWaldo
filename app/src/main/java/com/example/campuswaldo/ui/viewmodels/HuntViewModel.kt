package com.example.campuswaldo.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.campuswaldo.data.model.RedeemResult
import com.example.campuswaldo.data.model.WaldoOfDay
import com.example.campuswaldo.data.repository.WaldoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HuntUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val waldo: WaldoOfDay? = null,
    val codeInput: String = "",
    val redeemResult: RedeemResult? = null
)

@HiltViewModel
class HuntViewModel @Inject constructor(
    private val repository: WaldoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HuntUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    private val demoUserId = 8

    init {
        loadTodayWaldo()
        loadSecretCodeForTesting() // dev-only helper
    }

    /** Load today's Waldo from the backend instead of hard-coding it */
    private fun loadTodayWaldo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            repository.getTodayWaldo()
                .onSuccess { waldo ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        waldo = waldo
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load today's Waldo"
                    )
                }
        }
    }

    /** Dev only: fetch /waldo/code/ and print it to Logcat so you can test redeem. */
    private fun loadSecretCodeForTesting() {
        viewModelScope.launch {
            repository.getSecretCode()
                .onSuccess { code ->
                    Log.d("HuntViewModel", "Secret code for testing: $code")
                }
                .onFailure { e ->
                    Log.e("HuntViewModel", "Failed to load secret code", e)
                }
        }
    }

    /** Called when the user types in the code field */
    fun updateCodeInput(code: String) {
        _uiState.value = _uiState.value.copy(
            codeInput = code,
            redeemResult = null // clear previous result
        )
    }

    /** Called when the user taps the Redeem button */
    fun redeemCode() {
        val code = _uiState.value.codeInput.trim()
        if (code.isEmpty()) return

        viewModelScope.launch {
            repository.redeemCode(demoUserId, code)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        redeemResult = result,
                        error = null
                    )
                }
                .onFailure { e ->

                    val friendlyMessage = when {
                        e is retrofit2.HttpException && e.code() == 409 ->
                            "You already found today’s Waldo!"

                        e is retrofit2.HttpException && e.code() == 403 ->
                            "That code is incorrect. Keep hunting!"

                        else ->
                            e.message ?: "Redeem failed. Please try again."
                    }

                    _uiState.value = _uiState.value.copy(
                        redeemResult = null,
                        error = friendlyMessage
                    )
                }
        }
    }
}