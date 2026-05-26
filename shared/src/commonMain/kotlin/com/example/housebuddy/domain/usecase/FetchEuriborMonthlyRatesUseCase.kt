package com.example.housebuddy.domain.usecase

import com.example.housebuddy.data.repository.EcbExchangeRateRepository
import com.example.housebuddy.domain.model.MonthlyExchangeRate
import com.example.housebuddy.domain.util.roundToDecimals

class FetchEuriborMonthlyRatesUseCase(
    private val repository: EcbExchangeRateRepository = EcbExchangeRateRepository()
) {
    suspend operator fun invoke(): List<MonthlyExchangeRate> {
        val observations = repository.fetchEuriborObservations()
        if (observations.isEmpty()) {
            throw IllegalStateException("Nessuna osservazione nel file XML ECB")
        }
        return observations
            .mapNotNull { observation ->
                val yearMonth = observation.period.toYearMonth() ?: return@mapNotNull null
                MonthlyExchangeRate(
                    yearMonth = yearMonth,
                    averageRate = roundToDecimals(observation.value, 2)
                )
            }
            .sortedBy { it.yearMonth }
    }

    private fun String.toYearMonth(): String? {
        if (length == 7 && this[4] == '-') return this
        if (length >= 10 && this[4] == '-' && this[7] == '-') return substring(0, 7)
        return null
    }
}
