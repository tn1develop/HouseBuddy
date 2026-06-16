package com.example.housebuddy.domain.usecase

import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.model.SimulateScenariosInput
import com.example.housebuddy.domain.model.SimulateScenariosResult
import com.example.housebuddy.domain.util.defaultMortgageRate

class SimulateScenariosUseCase(
    private val calculateHousePriceUseCase: CalculateHousePriceUseCase = CalculateHousePriceUseCase()
) {
    operator fun invoke(input: SimulateScenariosInput): SimulateScenariosResult {
        val scenariosByYear = (0..MAX_YEAR).associateWith { year ->
            val availableLiquidity = input.currentLiquidity + year * input.annualSavings
            val scenario = findMortgageScenarioForLiquidity(availableLiquidity, input.housePriceInput, input.greenMortgage)
            val rentCost = input.rentPayment * 12.0 * year
            val totalCostWithRent = scenario?.second?.totalHouseCost?.plus(rentCost) ?: 0.0
            scenario?.second?.copy(totalHouseCost = totalCostWithRent)
        }
        return SimulateScenariosResult(scenariosByYear = scenariosByYear)
    }

    /**
     * Highest mortgage % (1–100) that still leaves [availableLiquidity] > [HousePriceResult.upfrontCashNeeded],
     * chosen so upfront costs use as much liquidity as possible (upfrontCashNeeded closest to liquidity from below).
     */
    private fun findMortgageScenarioForLiquidity(
        availableLiquidity: Double,
        housePriceInput: HousePriceInput,
        greenMortgage: Boolean
    ): Pair<Double, HousePriceResult>? {
        var bestMortgagePercent: Int? = null
        var bestResult: HousePriceResult? = null
        var smallestGap = Double.POSITIVE_INFINITY

        for (mortgagePercent in MIN_MORTGAGE_PERCENT..MAX_MORTGAGE_PERCENT) {
            val result = calculateHousePriceUseCase(
                housePriceInput.copy(
                    mortgageRequestInput = mortgagePercent.toString(),
                    mortgageRateInput = defaultMortgageRate(greenMortgage, mortgagePercent.toDouble())
                )
            )
            if (availableLiquidity > result.upfrontCashNeeded) {
                val gap = availableLiquidity - result.upfrontCashNeeded
                if (gap < smallestGap) {
                    smallestGap = gap
                    bestMortgagePercent = mortgagePercent
                    bestResult = result
                }
            }
        }

        val mortgagePercent = bestMortgagePercent ?: return null
        val result = bestResult ?: return null
        return mortgagePercent.toDouble() to result
    }

    private companion object {
        const val MAX_YEAR = 30
        const val MIN_MORTGAGE_PERCENT = 0
        const val MAX_MORTGAGE_PERCENT = 100
    }
}
