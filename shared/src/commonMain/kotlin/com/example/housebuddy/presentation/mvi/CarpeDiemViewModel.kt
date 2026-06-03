package com.example.housebuddy.presentation.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.housebuddy.domain.util.formatThousandsWithApostrophe
import com.example.housebuddy.domain.util.parseInputOrDefault

class CarpeDiemViewModel {
    var state by mutableStateOf(CarpeDiemViewState())
        private set

    fun handleEvent(event: CarpeDiemEvent) {
        state = when (event) {
            is CarpeDiemEvent.CanoneAffittoChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(canoneAffittoInput = normalized)
            }

            is CarpeDiemEvent.CanoneAffittoStepped -> {
                val current = parseInputOrDefault(state.canoneAffittoInput, 800.0)
                val next = (current + event.direction * 50.0).coerceIn(0.0, 10_000.0)
                state.copy(canoneAffittoInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is CarpeDiemEvent.LiquiditaAttualeChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(liquiditaAttualeInput = normalized)
            }

            is CarpeDiemEvent.LiquiditaAttualeStepped -> {
                val current = parseInputOrDefault(state.liquiditaAttualeInput, 20_000.0)
                val next = (current + event.direction * 1_000.0).coerceIn(0.0, 1_000_000.0)
                state.copy(liquiditaAttualeInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is CarpeDiemEvent.RisparmioAnnualeChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(risparmioAnnualeInput = normalized)
            }

            is CarpeDiemEvent.RisparmioAnnualeStepped -> {
                val current = parseInputOrDefault(state.risparmioAnnualeInput, 10_000.0)
                val next = (current + event.direction * 1_000.0).coerceIn(0.0, 500_000.0)
                state.copy(risparmioAnnualeInput = formatThousandsWithApostrophe(next.toInt()))
            }
        }
    }
}
