package com.example.housebuddy.data.local

import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.russhwolf.settings.Settings

class HousePriceStateStorage(
    private val settings: Settings = createHousePriceSettings()
) {
    fun load(): HousePriceViewState {
        val defaults = HousePriceViewState()
        return HousePriceViewState(
            housePriceInput = settings.getString(
                HousePriceSettingsKeys.HOUSE_PRICE_INPUT,
                defaults.housePriceInput
            ),
            mortgageRequestInput = settings.getString(
                HousePriceSettingsKeys.MORTGAGE_REQUEST_INPUT,
                defaults.mortgageRequestInput
            ),
            depositInput = settings.getString(
                HousePriceSettingsKeys.DEPOSIT_INPUT,
                defaults.depositInput
            ),
            agencyPercentageInput = settings.getString(
                HousePriceSettingsKeys.AGENCY_PERCENTAGE_INPUT,
                defaults.agencyPercentageInput
            ),
            agencyFixedFeeInput = settings.getString(
                HousePriceSettingsKeys.AGENCY_FIXED_FEE_INPUT,
                defaults.agencyFixedFeeInput
            ),
            greenMortgage = settings.getBoolean(
                HousePriceSettingsKeys.GREEN_MORTGAGE,
                defaults.greenMortgage
            ),
            isAgencyCommissionPercentage = settings.getBoolean(
                HousePriceSettingsKeys.IS_AGENCY_COMMISSION_PERCENTAGE,
                defaults.isAgencyCommissionPercentage
            ),
            numberOfBuyersInput = settings.getString(
                HousePriceSettingsKeys.NUMBER_OF_BUYERS_INPUT,
                defaults.numberOfBuyersInput
            ),
            mortgageRateInput = settings.getString(
                HousePriceSettingsKeys.MORTGAGE_RATE_INPUT,
                defaults.mortgageRateInput
            ),
            mortgageYearsInput = settings.getString(
                HousePriceSettingsKeys.MORTGAGE_YEARS_INPUT,
                defaults.mortgageYearsInput
            ),
            cadastralIncomeInput = settings.getString(
                HousePriceSettingsKeys.CADASTRAL_INCOME_INPUT,
                defaults.cadastralIncomeInput
            ),
            rentPaymentInput = settings.getString(
                HousePriceSettingsKeys.RENT_PAYMENT_INPUT,
                defaults.rentPaymentInput
            ),
            currentLiquidityInput = settings.getString(
                HousePriceSettingsKeys.CURRENT_LIQUIDITY_INPUT,
                defaults.currentLiquidityInput
            ),
            annualSavingsInput = settings.getString(
                HousePriceSettingsKeys.ANNUAL_SAVINGS_INPUT,
                defaults.annualSavingsInput
            )
        )
    }

    fun save(state: HousePriceViewState) {
        settings.putString(HousePriceSettingsKeys.HOUSE_PRICE_INPUT, state.housePriceInput)
        settings.putString(HousePriceSettingsKeys.MORTGAGE_REQUEST_INPUT, state.mortgageRequestInput)
        settings.putString(HousePriceSettingsKeys.DEPOSIT_INPUT, state.depositInput)
        settings.putString(HousePriceSettingsKeys.AGENCY_PERCENTAGE_INPUT, state.agencyPercentageInput)
        settings.putString(HousePriceSettingsKeys.AGENCY_FIXED_FEE_INPUT, state.agencyFixedFeeInput)
        settings.putBoolean(HousePriceSettingsKeys.GREEN_MORTGAGE, state.greenMortgage)
        settings.putBoolean(HousePriceSettingsKeys.IS_AGENCY_COMMISSION_PERCENTAGE, state.isAgencyCommissionPercentage)
        settings.putString(HousePriceSettingsKeys.NUMBER_OF_BUYERS_INPUT, state.numberOfBuyersInput)
        settings.putString(HousePriceSettingsKeys.MORTGAGE_RATE_INPUT, state.mortgageRateInput)
        settings.putString(HousePriceSettingsKeys.MORTGAGE_YEARS_INPUT, state.mortgageYearsInput)
        settings.putString(HousePriceSettingsKeys.CADASTRAL_INCOME_INPUT, state.cadastralIncomeInput)
        settings.putString(HousePriceSettingsKeys.RENT_PAYMENT_INPUT, state.rentPaymentInput)
        settings.putString(HousePriceSettingsKeys.CURRENT_LIQUIDITY_INPUT, state.currentLiquidityInput)
        settings.putString(HousePriceSettingsKeys.ANNUAL_SAVINGS_INPUT, state.annualSavingsInput)
    }
}
