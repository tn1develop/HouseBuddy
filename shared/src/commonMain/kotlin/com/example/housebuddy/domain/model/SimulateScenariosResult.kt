package com.example.housebuddy.domain.model

data class SimulateScenariosResult(
    val scenariosByYear: Map<Int, HousePriceResult?>
)
