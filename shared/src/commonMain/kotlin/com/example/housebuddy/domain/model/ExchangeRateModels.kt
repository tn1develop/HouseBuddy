package com.example.housebuddy.domain.model

data class ExchangeRateObservation(
    val period: String,
    val value: Double
)

data class MonthlyExchangeRate(
    val yearMonth: String,
    val averageRate: Double
)
