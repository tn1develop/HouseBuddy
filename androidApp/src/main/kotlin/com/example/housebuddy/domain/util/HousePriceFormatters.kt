package com.example.housebuddy.domain.util

import java.util.Locale

fun parseInputOrDefault(input: String, default: Double): Double {
    return input
        .replace("'", "")
        .replace(',', '.')
        .toDoubleOrNull() ?: default
}

fun formatThousandsWithApostrophe(value: Int): String {
    val sign = if (value < 0) "-" else ""
    val raw = kotlin.math.abs(value).toString()
    val grouped = raw.reversed().chunked(3).joinToString("'").reversed()
    return "$sign$grouped"
}

fun formatNumber(value: Double, decimals: Int): String {
    return if (decimals == 0) {
        value.toInt().toString()
    } else {
        String.format(Locale.US, "%.${decimals}f", value)
    }
}

fun formatEuroAmount(value: Double): String {
    val sign = if (value < 0) "-" else ""
    val absolute = kotlin.math.abs(value)
    val parts = String.format(Locale.US, "%.2f", absolute).split('.')
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

    val fattore = Math.pow(1 + tassoMensile, numeroRate.toDouble())
    return importo * (tassoMensile * fattore) / (fattore - 1)
}
