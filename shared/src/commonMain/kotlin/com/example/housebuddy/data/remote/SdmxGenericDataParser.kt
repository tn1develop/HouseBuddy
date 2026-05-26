package com.example.housebuddy.data.remote

import com.example.housebuddy.domain.model.ExchangeRateObservation

internal object SdmxGenericDataParser {
    private val observationPattern = Regex(
        """<generic:Obs>\s*<generic:ObsDimension value="([^"]+)"/>\s*<generic:ObsValue value="([^"]+)"/>"""
    )

    fun parseObservations(xml: String): List<ExchangeRateObservation> {
        return observationPattern.findAll(xml).mapNotNull { match ->
            val period = match.groupValues[1]
            val value = match.groupValues[2].toDoubleOrNull() ?: return@mapNotNull null
            ExchangeRateObservation(period = period, value = value)
        }.toList()
    }
}
