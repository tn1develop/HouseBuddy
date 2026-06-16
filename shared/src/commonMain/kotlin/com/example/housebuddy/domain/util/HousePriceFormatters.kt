package com.example.housebuddy.domain.util

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round

fun parsePositiveIntOrDefault(input: String, default: Int = 1, min: Int = 1): Int {
    return parseInputOrDefault(input, default.toDouble()).toInt().coerceAtLeast(min)
}

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

fun roundToDecimals(value: Double, decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(value * factor) / factor
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

/** mutuo green: Italian green mortgage; richiesta mutuo: mortgage request as % of house price */
fun defaultMortgageRate(greenMortgage: Boolean, mortgageRequestPercent: Double): String {
    return when {
        greenMortgage && mortgageRequestPercent <= 80.0 -> "2.59"
        greenMortgage && mortgageRequestPercent > 80.0 -> "2.86"
        !greenMortgage && mortgageRequestPercent <= 80.0 -> "2.99"
        else -> "3.52"
    }
}

/** rata mutuo: monthly mortgage installment */
fun calculateMonthlyMortgagePayment(
    amount: Double,
    annualRate: Double,
    durationYears: Int
): Double {
    if (amount <= 0 || annualRate < 0 || durationYears <= 0) return 0.0

    val monthlyRate = annualRate / 100.0 / 12.0
    val numberOfInstallments = durationYears * 12
    if (monthlyRate == 0.0) return amount / numberOfInstallments

    val factor = (1 + monthlyRate).pow(numberOfInstallments.toDouble())
    return amount * (monthlyRate * factor) / (factor - 1)
}
