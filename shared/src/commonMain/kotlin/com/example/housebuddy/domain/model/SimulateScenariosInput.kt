package com.example.housebuddy.domain.model

data class SimulateScenariosInput(
    val housePriceInput: HousePriceInput,
    val currentLiquidity: Double,
    val annualSavings: Double,
    /** canone affitto: monthly rent payment */
    val rentPayment: Double,
    /** mutuo green: Italian green mortgage */
    val greenMortgage: Boolean,
)
