package com.example.housebuddy.presentation.mvi

sealed interface HousePriceEvent {
    data class HousePriceChanged(val value: String) : HousePriceEvent
    data class HousePriceStepped(val direction: Int) : HousePriceEvent

    data class MortgageRequestChanged(val value: String) : HousePriceEvent
    data class MortgageRequestStepped(val direction: Int) : HousePriceEvent

    /** caparra: reservation deposit */
    data class DepositChanged(val value: String) : HousePriceEvent
    data class DepositStepped(val direction: Int) : HousePriceEvent

    data class AgencyPercentageChanged(val value: String) : HousePriceEvent
    data class AgencyPercentageStepped(val direction: Int) : HousePriceEvent

    data class AgencyFixedFeeChanged(val value: String) : HousePriceEvent
    data class AgencyFixedFeeStepped(val direction: Int) : HousePriceEvent

    /** mutuo green: Italian green mortgage */
    data class GreenMortgageChanged(val checked: Boolean) : HousePriceEvent
    data class AgencyCommissionTypeChanged(val isPercentage: Boolean) : HousePriceEvent
    data class NumberOfBuyersChanged(val value: String) : HousePriceEvent
    data class NumberOfBuyersStepped(val direction: Int) : HousePriceEvent

    data class MortgageRateChanged(val value: String) : HousePriceEvent
    data class MortgageRateStepped(val direction: Int) : HousePriceEvent

    data class MortgageYearsChanged(val value: String) : HousePriceEvent
    data class MortgageYearsStepped(val direction: Int) : HousePriceEvent

    /** rendita catastale: official cadastral income */
    data class CadastralIncomeChanged(val value: String) : HousePriceEvent
    data class CadastralIncomeStepped(val direction: Int) : HousePriceEvent

    data object ReloadFromStorage : HousePriceEvent

    /** canone affitto: monthly rent payment */
    data class RentPaymentChanged(val value: String) : HousePriceEvent
    data class RentPaymentStepped(val direction: Int) : HousePriceEvent

    data class CurrentLiquidityChanged(val value: String) : HousePriceEvent
    data class CurrentLiquidityStepped(val direction: Int) : HousePriceEvent

    data class AnnualSavingsChanged(val value: String) : HousePriceEvent
    data class AnnualSavingsStepped(val direction: Int) : HousePriceEvent
}
