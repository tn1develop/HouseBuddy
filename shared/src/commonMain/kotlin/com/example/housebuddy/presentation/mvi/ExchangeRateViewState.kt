package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.model.MonthlyExchangeRate

data class ExchangeRateViewState(
    val isLoading: Boolean = true,
    val monthlyRates: List<MonthlyExchangeRate> = emptyList(),
    val errorMessage: String? = null
)
