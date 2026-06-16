package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.SimulateScenariosInput
import com.example.housebuddy.domain.util.parseInputOrDefault

fun HousePriceViewState.toHousePriceInput() = HousePriceInput(
    housePriceInput = housePriceInput,
    mortgageRequestInput = mortgageRequestInput,
    depositInput = depositInput,
    agencyPercentageInput = agencyPercentageInput,
    agencyFixedFeeInput = agencyFixedFeeInput,
    isAgencyCommissionPercentage = isAgencyCommissionPercentage,
    mortgageRateInput = mortgageRateInput,
    mortgageYearsInput = mortgageYearsInput,
    cadastralIncomeInput = cadastralIncomeInput
)

fun HousePriceViewState.toSimulateScenariosInput() = SimulateScenariosInput(
    housePriceInput = toHousePriceInput(),
    currentLiquidity = parseInputOrDefault(currentLiquidityInput, 20_000.0),
    annualSavings = parseInputOrDefault(annualSavingsInput, 10_000.0),
    rentPayment = parseInputOrDefault(rentPaymentInput, 800.0),
    greenMortgage = greenMortgage
)
