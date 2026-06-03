package com.example.housebuddy.presentation.mvi

sealed interface CarpeDiemEvent {
    data class CanoneAffittoChanged(val value: String) : CarpeDiemEvent
    data class CanoneAffittoStepped(val direction: Int) : CarpeDiemEvent

    data class LiquiditaAttualeChanged(val value: String) : CarpeDiemEvent
    data class LiquiditaAttualeStepped(val direction: Int) : CarpeDiemEvent

    data class RisparmioAnnualeChanged(val value: String) : CarpeDiemEvent
    data class RisparmioAnnualeStepped(val direction: Int) : CarpeDiemEvent
}
