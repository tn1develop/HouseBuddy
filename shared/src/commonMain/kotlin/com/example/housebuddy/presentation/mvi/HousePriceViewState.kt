package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.util.formatThousandsWithApostrophe
import com.example.housebuddy.domain.util.tassoDefault

data class HousePriceViewState(
    val prezzoCasaInput: String = formatThousandsWithApostrophe(139000),
    val anticipoInput: String = "20",
    val caparraInput: String = "5000",
    val percentualeAgenziaInput: String = "5",
    val fissoAgenziaInput: String = "7000",
    val mutuoGreen: Boolean = false,
    val isPercentuale: Boolean = false,
    val isTotalExpenses: Boolean = true, //total or pro capite, meaning divided by 2
    val tassoMutuoInput: String = tassoDefault(false, 20.0),
    val anniMutuoInput: String = "30",
    val renditaCatastaleInput: String = "1071"
)
