package com.example.housebuddy.presentation.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.housebuddy.data.local.HousePriceStateStorage
import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.usecase.CalculateHousePriceUseCase
import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.domain.util.formatThousandsWithApostrophe
import com.example.housebuddy.domain.util.parseInputOrDefault
import com.example.housebuddy.domain.util.parsePositiveIntOrDefault
import com.example.housebuddy.domain.util.tassoDefault
import kotlin.math.round

class HousePriceViewModel(
    private val calculateHousePriceUseCase: CalculateHousePriceUseCase = CalculateHousePriceUseCase(),
    private val stateStorage: HousePriceStateStorage = HousePriceStateStorage()
) {
    var state by mutableStateOf(stateStorage.load())
        private set

    val result: HousePriceResult
        get() = calculateHousePriceUseCase(
            HousePriceInput(
                prezzoCasaInput = state.prezzoCasaInput,
                anticipoInput = state.anticipoInput,
                caparraInput = state.caparraInput,
                percentualeAgenziaInput = state.percentualeAgenziaInput,
                fissoAgenziaInput = state.fissoAgenziaInput,
                isPercentuale = state.isPercentuale,
                tassoMutuoInput = state.tassoMutuoInput,
                anniMutuoInput = state.anniMutuoInput,
                renditaCatastaleInput = state.renditaCatastaleInput
            )
        )

    fun handleEvent(event: HousePriceEvent) {
        val newState = when (event) {
            is HousePriceEvent.PrezzoCasaChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) "" else formatThousandsWithApostrophe(digitsOnly.toInt())
                state.copy(prezzoCasaInput = normalized)
            }

            is HousePriceEvent.PrezzoCasaStepped -> {
                val current = parseInputOrDefault(state.prezzoCasaInput, 150000.0)
                val next = (current + event.direction * 1000.0).coerceIn(80000.0, 300000.0)
                state.copy(prezzoCasaInput = formatThousandsWithApostrophe(next.toInt()))
            }

            is HousePriceEvent.AnticipoChanged ->
                state.copy(anticipoInput = event.value)

            is HousePriceEvent.AnticipoStepped -> {
                val current = parseInputOrDefault(state.anticipoInput, 20.0)
                val next = (current + event.direction * 5.0).coerceIn(0.0, 30.0)
                state.copy(
                    anticipoInput = formatNumber(next, 0),
                    tassoMutuoInput = tassoDefault(state.mutuoGreen, next)
                )
            }

            is HousePriceEvent.CaparraChanged ->
                state.copy(caparraInput = event.value)

            is HousePriceEvent.CaparraStepped -> {
                val current = parseInputOrDefault(state.caparraInput, 5000.0)
                val next = (current + event.direction * 500.0).coerceIn(0.0, 50000.0)
                state.copy(caparraInput = formatNumber(next, 0))
            }

            is HousePriceEvent.PercentualeAgenziaChanged ->
                state.copy(percentualeAgenziaInput = event.value)

            is HousePriceEvent.PercentualeAgenziaStepped -> {
                val current = parseInputOrDefault(state.percentualeAgenziaInput, 5.0)
                val next = (current + event.direction).coerceIn(0.0, 10.0)
                state.copy(percentualeAgenziaInput = formatNumber(next, 0))
            }

            is HousePriceEvent.FissoAgenziaChanged ->
                state.copy(fissoAgenziaInput = event.value)

            is HousePriceEvent.FissoAgenziaStepped -> {
                val current = parseInputOrDefault(state.fissoAgenziaInput, 7000.0)
                val next = (current + event.direction * 1000.0).coerceIn(0.0, 10000.0)
                state.copy(fissoAgenziaInput = formatNumber(next, 0))
            }

            is HousePriceEvent.MutuoGreenChanged -> {
                val anticipo = parseInputOrDefault(state.anticipoInput, 20.0).coerceIn(0.0, 30.0)
                state.copy(
                    mutuoGreen = event.checked,
                    tassoMutuoInput = tassoDefault(event.checked, anticipo)
                )
            }

            is HousePriceEvent.IsPercentualeChanged ->
                state.copy(isPercentuale = event.checked)

            is HousePriceEvent.NumeroCompratoriChanged -> {
                val digitsOnly = event.value.filter { it.isDigit() }
                val normalized = if (digitsOnly.isEmpty()) {
                    ""
                } else {
                    digitsOnly.toInt().coerceIn(1, 10).toString()
                }
                state.copy(numeroCompratoriInput = normalized)
            }

            is HousePriceEvent.NumeroCompratoriStepped -> {
                val current = parsePositiveIntOrDefault(state.numeroCompratoriInput)
                val next = (current + event.direction).coerceIn(1, 10)
                state.copy(numeroCompratoriInput = next.toString())
            }

            is HousePriceEvent.TassoMutuoChanged ->
                state.copy(tassoMutuoInput = event.value)

            is HousePriceEvent.TassoMutuoStepped -> {
                val defaultRate = if (state.mutuoGreen) 2.59 else 2.99
                val current = parseInputOrDefault(state.tassoMutuoInput, defaultRate)
                val next = (current + event.direction * 0.5).coerceIn(1.0, 5.0)
                val rounded = round(next * 100.0) / 100.0
                state.copy(tassoMutuoInput = formatNumber(rounded, 2))
            }

            is HousePriceEvent.AnniMutuoChanged ->
                state.copy(anniMutuoInput = event.value)

            is HousePriceEvent.AnniMutuoStepped -> {
                val current = parseInputOrDefault(state.anniMutuoInput, 30.0)
                val next = (current + event.direction * 5.0).toInt().coerceIn(5, 40)
                state.copy(anniMutuoInput = next.toString())
            }

            is HousePriceEvent.RenditaCatastaleChanged ->
                state.copy(renditaCatastaleInput = event.value)

            is HousePriceEvent.RenditaCatastaleStepped -> {
                val current = parseInputOrDefault(state.renditaCatastaleInput, 30.0)
                val next = (current + event.direction).coerceIn(0.0, 4000.0)
                state.copy(renditaCatastaleInput = next.toString())
            }
        }
        state = newState
        stateStorage.save(newState)
    }
}
