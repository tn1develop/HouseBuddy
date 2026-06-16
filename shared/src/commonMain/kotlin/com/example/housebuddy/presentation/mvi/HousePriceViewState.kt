package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.util.defaultMortgageRate
import com.example.housebuddy.domain.util.formatThousandsWithApostrophe

data class HousePriceViewState(
    val housePriceInput: String = formatThousandsWithApostrophe(139000),
    val mortgageRequestInput: String = "80",
    /** caparra: reservation deposit paid to the seller */
    val depositInput: String = "5000",
    val agencyPercentageInput: String = "5",
    val agencyFixedFeeInput: String = "7000",
    /** mutuo green: Italian green mortgage for energy-efficient homes (class A or B) */
    val greenMortgage: Boolean = false,
    val isAgencyCommissionPercentage: Boolean = false,
    val numberOfBuyersInput: String = "1",
    val mortgageRateInput: String = defaultMortgageRate(false, 80.0),
    val mortgageYearsInput: String = "30",
    /** rendita catastale: official cadastral income assigned to the property */
    val cadastralIncomeInput: String = "1071",
    /** canone affitto: monthly rent payment */
    val rentPaymentInput: String = formatThousandsWithApostrophe(800),
    val currentLiquidityInput: String = formatThousandsWithApostrophe(20_000),
    val annualSavingsInput: String = formatThousandsWithApostrophe(10_000)
)
