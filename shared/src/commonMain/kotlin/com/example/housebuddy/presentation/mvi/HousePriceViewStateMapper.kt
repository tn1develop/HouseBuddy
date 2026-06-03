package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.SimulateScenariosInput
import com.example.housebuddy.domain.util.parseInputOrDefault

fun HousePriceViewState.toHousePriceInput() = HousePriceInput(
    prezzoCasaInput = prezzoCasaInput,
    richiestaMutuoInput = richiestaMutuoInput,
    caparraInput = caparraInput,
    percentualeAgenziaInput = percentualeAgenziaInput,
    fissoAgenziaInput = fissoAgenziaInput,
    isPercentuale = isPercentuale,
    tassoMutuoInput = tassoMutuoInput,
    anniMutuoInput = anniMutuoInput,
    renditaCatastaleInput = renditaCatastaleInput
)

fun HousePriceViewState.toSimulateScenariosInput() = SimulateScenariosInput(
    housePriceInput = toHousePriceInput(),
    liquiditaAttuale = parseInputOrDefault(liquiditaAttualeInput, 20_000.0),
    risparmioAnnuale = parseInputOrDefault(risparmioAnnualeInput, 10_000.0),
    canoneAffitto = parseInputOrDefault(canoneAffittoInput, 800.0),
    mutuoGreen = mutuoGreen
)
