package com.example.housebuddy.presentation.mvi

sealed interface HousePriceEvent {
    data class PrezzoCasaChanged(val value: String) : HousePriceEvent
    data class PrezzoCasaStepped(val direction: Int) : HousePriceEvent

    data class RichiestaMutuoChanged(val value: String) : HousePriceEvent
    data class RichiestaMutuoStepped(val direction: Int) : HousePriceEvent

    data class CaparraChanged(val value: String) : HousePriceEvent
    data class CaparraStepped(val direction: Int) : HousePriceEvent

    data class PercentualeAgenziaChanged(val value: String) : HousePriceEvent
    data class PercentualeAgenziaStepped(val direction: Int) : HousePriceEvent

    data class FissoAgenziaChanged(val value: String) : HousePriceEvent
    data class FissoAgenziaStepped(val direction: Int) : HousePriceEvent

    data class MutuoGreenChanged(val checked: Boolean) : HousePriceEvent
    data class IsPercentualeChanged(val checked: Boolean) : HousePriceEvent
    data class NumeroCompratoriChanged(val value: String) : HousePriceEvent
    data class NumeroCompratoriStepped(val direction: Int) : HousePriceEvent

    data class TassoMutuoChanged(val value: String) : HousePriceEvent
    data class TassoMutuoStepped(val direction: Int) : HousePriceEvent

    data class AnniMutuoChanged(val value: String) : HousePriceEvent
    data class AnniMutuoStepped(val direction: Int) : HousePriceEvent

    data class RenditaCatastaleChanged(val value: String) : HousePriceEvent
    data class RenditaCatastaleStepped(val direction: Int) : HousePriceEvent

    data object ReloadFromStorage : HousePriceEvent

    data class CanoneAffittoChanged(val value: String) : HousePriceEvent
    data class CanoneAffittoStepped(val direction: Int) : HousePriceEvent

    data class LiquiditaAttualeChanged(val value: String) : HousePriceEvent
    data class LiquiditaAttualeStepped(val direction: Int) : HousePriceEvent

    data class RisparmioAnnualeChanged(val value: String) : HousePriceEvent
    data class RisparmioAnnualeStepped(val direction: Int) : HousePriceEvent
}
