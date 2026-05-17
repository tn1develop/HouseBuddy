package com.example.housepricescalculator.presentation.mvi

import com.example.housepricescalculator.domain.util.formatThousandsWithApostrophe
import com.example.housepricescalculator.domain.util.tassoDefault

data class HousePriceViewState(
    val showAdvancedFields: Boolean = false,
    val prezzoCasaInput: String = formatThousandsWithApostrophe(139000),
    val anticipoInput: String = "20",
    val percentualeAgenziaInput: String = "5",
    val fissoAgenziaInput: String = "7000",
    val mutuoGreen: Boolean = false,
    val isPercentuale: Boolean = false,
    val tassoMutuoInput: String = tassoDefault(false, 20.0),
    val anniMutuoInput: String = "30",
    val renditaCatastaleInput: String = "1071"
)
