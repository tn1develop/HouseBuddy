package com.example.housebuddy.presentation.mvi

sealed interface ExchangeRateEvent {
    data object ScreenOpened : ExchangeRateEvent
    data object RetryClicked : ExchangeRateEvent
}
