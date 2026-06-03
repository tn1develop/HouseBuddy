package com.example.housebuddy.presentation.mvi

import com.example.housebuddy.domain.util.formatThousandsWithApostrophe

data class CarpeDiemViewState(
    val canoneAffittoInput: String = formatThousandsWithApostrophe(800),
    val liquiditaAttualeInput: String = formatThousandsWithApostrophe(20_000),
    val risparmioAnnualeInput: String = formatThousandsWithApostrophe(10_000)
)
