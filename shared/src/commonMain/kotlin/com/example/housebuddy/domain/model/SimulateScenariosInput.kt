package com.example.housebuddy.domain.model

data class SimulateScenariosInput(
    val housePriceInput: HousePriceInput,
    val liquiditaAttuale: Double,
    val risparmioAnnuale: Double,
    val canoneAffitto: Double,
    val mutuoGreen: Boolean,
)
