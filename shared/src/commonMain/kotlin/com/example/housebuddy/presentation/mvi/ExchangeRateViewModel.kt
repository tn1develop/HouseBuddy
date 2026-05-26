package com.example.housebuddy.presentation.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.housebuddy.domain.usecase.FetchEuriborMonthlyRatesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ExchangeRateViewModel(
    private val fetchEuriborMonthlyRates: FetchEuriborMonthlyRatesUseCase = FetchEuriborMonthlyRatesUseCase()
) {
    private val scope = CoroutineScope(SupervisorJob())

    var state by mutableStateOf(ExchangeRateViewState())
        private set

    fun handleEvent(event: ExchangeRateEvent) {
        when (event) {
            ExchangeRateEvent.ScreenOpened,
            ExchangeRateEvent.RetryClicked -> loadRates()
        }
    }

    private fun loadRates() {
        scope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            runCatching { fetchEuriborMonthlyRates() }
                .onSuccess { rates ->
                    state = state.copy(
                        isLoading = false,
                        monthlyRates = rates,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    state = state.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Errore nel caricamento dei dati"
                    )
                }
        }
    }
}
