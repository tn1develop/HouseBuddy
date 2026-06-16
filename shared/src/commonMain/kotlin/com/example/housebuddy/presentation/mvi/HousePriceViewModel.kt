package com.example.housebuddy.presentation.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.housebuddy.data.local.HousePriceStateStorage
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.model.SimulateScenariosResult
import com.example.housebuddy.domain.usecase.CalculateHousePriceUseCase
import com.example.housebuddy.domain.usecase.SimulateScenariosUseCase
import com.example.housebuddy.domain.util.defaultMortgageRate
import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.domain.util.formatThousandsWithApostrophe
import com.example.housebuddy.domain.util.parseInputOrDefault
import com.example.housebuddy.domain.util.parsePositiveIntOrDefault
import kotlin.math.round

class HousePriceViewModel(
    private val calculateHousePriceUseCase: CalculateHousePriceUseCase = CalculateHousePriceUseCase(),
    private val simulateScenariosUseCase: SimulateScenariosUseCase = SimulateScenariosUseCase(),
    private val stateStorage: HousePriceStateStorage = HousePriceStateStorage()
) {
    var state by mutableStateOf(stateStorage.load())
        private set

    val result: HousePriceResult
        get() = calculateHousePriceUseCase(state.toHousePriceInput())

    val scenarioResult: SimulateScenariosResult
        get() = simulateScenariosUseCase(state.toSimulateScenariosInput())

    fun handleEvent(event: HousePriceEvent) {
        val newState = when (event) {
            is HousePriceEvent.HousePriceChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(housePriceInput = normalized)
            }

            is HousePriceEvent.HousePriceStepped -> {
                val current = parseInputOrDefault(state.housePriceInput, 150000.0)
                val next = (current + event.direction * 1000.0).coerceAtLeast(0.0)
                state.copy(housePriceInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is HousePriceEvent.MortgageRequestChanged ->
                state.copy(mortgageRequestInput = event.value)

            is HousePriceEvent.MortgageRequestStepped -> {
                val current = parseInputOrDefault(state.mortgageRequestInput, 80.0)
                val next = (current + event.direction * 5.0).coerceAtLeast(0.0)
                state.copy(
                    mortgageRequestInput = formatNumber(next, 0),
                    mortgageRateInput = defaultMortgageRate(state.greenMortgage, next)
                )
            }

            is HousePriceEvent.DepositChanged ->
                state.copy(depositInput = event.value)

            is HousePriceEvent.DepositStepped -> {
                val current = parseInputOrDefault(state.depositInput, 5000.0)
                val next = (current + event.direction * 500.0).coerceIn(0.0, 50000.0)
                state.copy(depositInput = formatNumber(next, 0))
            }

            is HousePriceEvent.AgencyPercentageChanged ->
                state.copy(agencyPercentageInput = event.value)

            is HousePriceEvent.AgencyPercentageStepped -> {
                val current = parseInputOrDefault(state.agencyPercentageInput, 5.0)
                val next = (current + event.direction).coerceAtLeast(0.0)
                state.copy(agencyPercentageInput = formatNumber(next, 0))
            }

            is HousePriceEvent.AgencyFixedFeeChanged ->
                state.copy(agencyFixedFeeInput = event.value)

            is HousePriceEvent.AgencyFixedFeeStepped -> {
                val current = parseInputOrDefault(state.agencyFixedFeeInput, 7000.0)
                val next = (current + event.direction * 1000.0).coerceAtLeast(0.0)
                state.copy(agencyFixedFeeInput = formatNumber(next, 0))
            }

            is HousePriceEvent.GreenMortgageChanged -> {
                val mortgageRequestPercent = parseInputOrDefault(state.mortgageRequestInput, 80.0).coerceIn(1.0, 100.0)
                state.copy(
                    greenMortgage = event.checked,
                    mortgageRateInput = defaultMortgageRate(event.checked, mortgageRequestPercent)
                )
            }

            is HousePriceEvent.AgencyCommissionTypeChanged ->
                state.copy(isAgencyCommissionPercentage = event.isPercentage)

            is HousePriceEvent.NumberOfBuyersChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) {
                    ""
                } else {
                    digitsOnly.toInt().coerceIn(1, 10).toString()
                }
                state.copy(numberOfBuyersInput = normalized)
            }

            is HousePriceEvent.NumberOfBuyersStepped -> {
                val current = parsePositiveIntOrDefault(state.numberOfBuyersInput)
                val next = (current + event.direction).coerceIn(1, 10)
                state.copy(numberOfBuyersInput = next.toString())
            }

            is HousePriceEvent.MortgageRateChanged ->
                state.copy(mortgageRateInput = event.value)

            is HousePriceEvent.MortgageRateStepped -> {
                val defaultRate = if (state.greenMortgage) 2.59 else 2.99
                val current = parseInputOrDefault(state.mortgageRateInput, defaultRate)
                val next = (current + event.direction * 0.5).coerceAtLeast(0.0)
                val rounded = round(next * 100.0) / 100.0
                state.copy(mortgageRateInput = formatNumber(rounded, 2))
            }

            is HousePriceEvent.MortgageYearsChanged ->
                state.copy(mortgageYearsInput = event.value)

            is HousePriceEvent.MortgageYearsStepped -> {
                val current = parseInputOrDefault(state.mortgageYearsInput, 30.0)
                val next = (current + event.direction * 5.0).toInt().coerceIn(5, 40)
                state.copy(mortgageYearsInput = next.toString())
            }

            is HousePriceEvent.CadastralIncomeChanged ->
                state.copy(cadastralIncomeInput = event.value)

            is HousePriceEvent.CadastralIncomeStepped -> {
                val current = parseInputOrDefault(state.cadastralIncomeInput, 30.0)
                val next = (current + event.direction).coerceAtLeast(0.0)
                state.copy(cadastralIncomeInput = next.toString())
            }

            HousePriceEvent.ReloadFromStorage -> stateStorage.load()

            is HousePriceEvent.RentPaymentChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(rentPaymentInput = normalized)
            }

            is HousePriceEvent.RentPaymentStepped -> {
                val current = parseInputOrDefault(state.rentPaymentInput, 800.0)
                val next = (current + event.direction * 50.0).coerceIn(0.0, 10_000.0)
                state.copy(rentPaymentInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is HousePriceEvent.CurrentLiquidityChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(currentLiquidityInput = normalized)
            }

            is HousePriceEvent.CurrentLiquidityStepped -> {
                val current = parseInputOrDefault(state.currentLiquidityInput, 20_000.0)
                val next = (current + event.direction * 1_000.0).coerceIn(0.0, 1_000_000.0)
                state.copy(currentLiquidityInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is HousePriceEvent.AnnualSavingsChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(annualSavingsInput = normalized)
            }

            is HousePriceEvent.AnnualSavingsStepped -> {
                val current = parseInputOrDefault(state.annualSavingsInput, 10_000.0)
                val next = (current + event.direction * 1_000.0).coerceIn(0.0, 500_000.0)
                state.copy(annualSavingsInput = formatThousandsWithApostrophe(next.toInt()))
            }
        }
        state = newState
        stateStorage.save(newState)
    }
}
