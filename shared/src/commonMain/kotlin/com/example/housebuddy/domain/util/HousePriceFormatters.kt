package com.example.housebuddy.domain.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

fun parseInputOrDefault(input: String, default: Double): Double {
    return input
        .replace("'", "")
        .replace(',', '.')
        .toDoubleOrNull() ?: default
}

fun formatThousandsWithApostrophe(value: Int): String {
    val sign = if (value < 0) "-" else ""
    val raw = abs(value).toString()
    val grouped = raw.reversed().chunked(3).joinToString("'").reversed()
    return "$sign$grouped"
}

fun formatNumber(value: Double, decimals: Int): String {
    if (decimals == 0) {
        return value.toInt().toString()
    }
    val factor = 10.0.pow(decimals)
    val rounded = round(value * factor) / factor
    val str = rounded.toString()
    val dotIndex = str.indexOf('.')
    return if (dotIndex == -1) {
        "$str.${"0".repeat(decimals)}"
    } else {
        val intPart = str.substring(0, dotIndex)
        val decPart = str.substring(dotIndex + 1).padEnd(decimals, '0').take(decimals)
        "$intPart.$decPart"
    }
}

fun formatEuroAmount(value: Double): String {
    val sign = if (value < 0) "-" else ""
    val absolute = abs(value)
    val parts = formatNumber(absolute, 2).split('.')
    val integerPart = parts[0].reversed().chunked(3).joinToString("'").reversed()
    return "€ $sign$integerPart.${parts[1]}"
}

fun tassoDefault(mutuoGreen: Boolean, anticipo: Double): String {
    return when {
        mutuoGreen && anticipo >= 20.0 -> "2.59"
        mutuoGreen && anticipo < 20.0 -> "2.86"
        !mutuoGreen && anticipo >= 20.0 -> "2.99"
        else -> "3.52"
    }
}

fun calcolaRataMutuo(
    importo: Double,
    tassoAnnuo: Double,
    durataAnni: Int
): Double {
    if (importo <= 0 || tassoAnnuo < 0 || durataAnni <= 0) return 0.0

    val tassoMensile = tassoAnnuo / 100.0 / 12.0
    val numeroRate = durataAnni * 12
    if (tassoMensile == 0.0) return importo / numeroRate

    val fattore = (1 + tassoMensile).pow(numeroRate.toDouble())
    return importo * (tassoMensile * fattore) / (fattore - 1)
}
