package com.example.housebuddy.domain.usecase

import com.example.housebuddy.domain.model.HousePriceBreakdown
import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.util.calculateMonthlyMortgagePayment
import com.example.housebuddy.domain.util.parseInputOrDefault

class CalculateHousePriceUseCase {
    operator fun invoke(input: HousePriceInput): HousePriceResult {
        val housePrice = parseInputOrDefault(input.housePriceInput, 150000.0).coerceAtLeast(0.0)
        val mortgageRequestPercent = parseInputOrDefault(input.mortgageRequestInput, 80.0).coerceIn(0.0, 100.0)
        val downPaymentPercent = 100.0 - mortgageRequestPercent
        /** caparra: reservation deposit */
        val deposit = parseInputOrDefault(input.depositInput, 5000.0).coerceAtLeast(0.0)
        val agencyPercentage = parseInputOrDefault(input.agencyPercentageInput, 4.0).coerceAtLeast(0.0)
        val agencyFixedFee = parseInputOrDefault(input.agencyFixedFeeInput, 7000.0).coerceAtLeast(0.0)
        val mortgageRate = parseInputOrDefault(input.mortgageRateInput, 2.99).coerceAtLeast(0.0)
        val mortgageYears = parseInputOrDefault(input.mortgageYearsInput, 30.0).toInt().coerceIn(5, 40)
        /** rendita catastale: official cadastral income */
        val cadastralIncome = parseInputOrDefault(input.cadastralIncomeInput, 1071.0).coerceAtLeast(0.0)

        val registrationTax = cadastralIncome * 1.05 * 110 * 0.02
        val mortgageRegistryTax = 50.0
        val cadastralTax = 50.0
        val archiveFee = 30.4
        val notaryDraftingAndCopyFees = 1206.73
        val notaryRegulatoryContributions = 145.4
        val mortgageRegistrySearches = 200.0
        val purchaseTaxesAndNotaryFees = registrationTax + mortgageRegistryTax + cadastralTax + archiveFee +
            notaryDraftingAndCopyFees + notaryRegulatoryContributions + mortgageRegistrySearches
        /** istruttoria: mortgage application fee */
        val applicationFee = 1000.0
        /** imposta sostitutiva: flat substitute tax on the mortgage */
        val substituteTax = 300.0
        /** perizia: property appraisal */
        val appraisal = 300.0
        val mandatoryFireInsurance = 650.0
        val lifeInsurance = 150.0
        val mortgageLoanAmount = housePrice * (mortgageRequestPercent / 100.0)
        /** notaio mutuo: notary fees for the mortgage deed */
        val mortgageNotaryFee = mortgageLoanAmount * 0.00577 + 400.0
        val mortgageStartupCosts = applicationFee + substituteTax + appraisal + mandatoryFireInsurance + lifeInsurance + mortgageNotaryFee
        val agencyCommission = if (input.isAgencyCommissionPercentage) housePrice * (agencyPercentage / 100.0) else agencyFixedFee
        val downPaymentMinusDeposit = housePrice * (downPaymentPercent / 100.0) - deposit
        val agencyFeeWithVat = agencyCommission * 1.22
        /** soldi subito: cash needed at or before closing */
        val upfrontCashNeeded = agencyFeeWithVat + purchaseTaxesAndNotaryFees + mortgageStartupCosts + housePrice * (downPaymentPercent / 100.0)
        /** rata mutuo: monthly mortgage installment */
        val monthlyMortgagePayment = calculateMonthlyMortgagePayment(mortgageLoanAmount, annualRate = mortgageRate, durationYears = mortgageYears)
        val totalHouseCost = upfrontCashNeeded + (monthlyMortgagePayment * 12.0 * mortgageYears)

        return HousePriceResult(
            upfrontCashNeeded = upfrontCashNeeded,
            mortgageLoanAmount = mortgageLoanAmount,
            monthlyMortgagePayment = monthlyMortgagePayment,
            totalHouseCost = totalHouseCost,
            breakdown = HousePriceBreakdown(
                deposit = deposit,
                downPaymentMinusDeposit = downPaymentMinusDeposit,
                agencyFeeWithVat = agencyFeeWithVat,
                registrationTax = registrationTax,
                mortgageRegistryTax = mortgageRegistryTax,
                cadastralTax = cadastralTax,
                archiveFee = archiveFee,
                notaryDraftingAndCopyFees = notaryDraftingAndCopyFees,
                notaryRegulatoryContributions = notaryRegulatoryContributions,
                mortgageRegistrySearches = mortgageRegistrySearches,
                applicationFee = applicationFee,
                substituteTax = substituteTax,
                appraisal = appraisal,
                mandatoryFireInsurance = mandatoryFireInsurance,
                lifeInsurance = lifeInsurance,
                mortgageNotaryFee = mortgageNotaryFee
            )
        )
    }
}
