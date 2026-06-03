package com.example.housebuddy.domain.usecase

import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.model.SimulateScenariosInput
import com.example.housebuddy.domain.model.SimulateScenariosResult
import com.example.housebuddy.domain.util.tassoDefault

class SimulateScenariosUseCase(
    private val calculateHousePriceUseCase: CalculateHousePriceUseCase = CalculateHousePriceUseCase()
) {
    operator fun invoke(input: SimulateScenariosInput): SimulateScenariosResult {
        val scenariosByYear = (0..MAX_YEAR).associateWith { year ->
            val liquiditaDisponibile = input.liquiditaAttuale + year * input.risparmioAnnuale
            val scenario = findMortgageScenarioForLiquidity(liquiditaDisponibile, input.housePriceInput, input.mutuoGreen)
            val costoAffitto = input.canoneAffitto * 12.0 * year
            val costoTotaleConAffitto = scenario?.second?.costoTotaleCasa?.plus(costoAffitto) ?:0.0
            scenario?.second?.copy(costoTotaleCasa = costoTotaleConAffitto)
        }
        return SimulateScenariosResult(scenariosByYear = scenariosByYear)
    }

    /**
     * Highest mortgage % (1–100) that still leaves [liquiditaDisponibile] > [HousePriceResult.soldiSubito],
     * chosen so upfront costs use as much liquidity as possible (soldiSubito closest to liquidity from below).
     */
    private fun findMortgageScenarioForLiquidity(
        liquiditaDisponibile: Double,
        housePriceInput: HousePriceInput,
        mutuoGreen: Boolean
    ): Pair<Double, HousePriceResult>? {
        var bestMutuoPercent: Int? = null
        var bestResult: HousePriceResult? = null
        var smallestGap = Double.POSITIVE_INFINITY

        for (mutuoPercent in MIN_MUTUO_PERCENT..MAX_MUTUO_PERCENT) {

            val result = calculateHousePriceUseCase(
                housePriceInput.copy(richiestaMutuoInput = mutuoPercent.toString(), tassoMutuoInput = tassoDefault(mutuoGreen, mutuoPercent.toDouble()))
            )
            if (liquiditaDisponibile > result.soldiSubito) {
                val gap = liquiditaDisponibile - result.soldiSubito
                if (gap < smallestGap) {
                    smallestGap = gap
                    bestMutuoPercent = mutuoPercent
                    bestResult = result
                }
            }
        }

        val mutuoPercent = bestMutuoPercent ?: return null
        val result = bestResult ?: return null
        return mutuoPercent.toDouble() to result
    }

    private companion object {
        const val MAX_YEAR = 30
        const val MIN_MUTUO_PERCENT = 0
        const val MAX_MUTUO_PERCENT = 100
    }
}
