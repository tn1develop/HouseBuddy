package com.example.housebuddy.domain.model

data class HousePriceInput(
    val housePriceInput: String,
    val mortgageRequestInput: String,
    /** caparra: reservation deposit paid to the seller */
    val depositInput: String,
    val agencyPercentageInput: String,
    val agencyFixedFeeInput: String,
    val isAgencyCommissionPercentage: Boolean,
    val mortgageRateInput: String,
    val mortgageYearsInput: String,
    /** rendita catastale: official cadastral income assigned to the property */
    val cadastralIncomeInput: String
)

data class HousePriceBreakdown(
    /** caparra: reservation deposit paid to the seller */
    val deposit: Double,
    val downPaymentMinusDeposit: Double,
    val agencyFeeWithVat: Double,
    /** imposta di registro: registration tax */
    val registrationTax: Double,
    /** imposta ipotecaria: mortgage registry tax */
    val mortgageRegistryTax: Double,
    /** imposta catastale: cadastral tax */
    val cadastralTax: Double,
    val archiveFee: Double,
    val notaryDraftingAndCopyFees: Double,
    val notaryRegulatoryContributions: Double,
    /** visure ipotecarie: mortgage registry searches */
    val mortgageRegistrySearches: Double,
    /** istruttoria: mortgage application fee */
    val applicationFee: Double,
    /** imposta sostitutiva: flat substitute tax on the mortgage */
    val substituteTax: Double,
    /** perizia: property appraisal */
    val appraisal: Double,
    val mandatoryFireInsurance: Double,
    val lifeInsurance: Double,
    /** notaio mutuo: notary fees for the mortgage deed */
    val mortgageNotaryFee: Double
)

data class HousePriceResult(
    /** soldi subito: cash needed at or before closing */
    val upfrontCashNeeded: Double,
    val mortgageLoanAmount: Double,
    /** rata mutuo: monthly mortgage installment */
    val monthlyMortgagePayment: Double,
    val totalHouseCost: Double,
    val breakdown: HousePriceBreakdown
)
