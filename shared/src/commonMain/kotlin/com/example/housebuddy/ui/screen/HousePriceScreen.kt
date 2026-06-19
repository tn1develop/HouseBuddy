package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.util.formatEuroAmount
import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.domain.util.parseInputOrDefault
import com.example.housebuddy.domain.util.parsePositiveIntOrDefault
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.InfoIcon
import com.example.housebuddy.ui.components.ResultFieldCompact
import com.example.housebuddy.ui.components.StepperInputField
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.agency_fixed_label
import housebuddy.shared.generated.resources.agency_percentage_label
import housebuddy.shared.generated.resources.expense_label_per_capita
import housebuddy.shared.generated.resources.expense_label_total
import housebuddy.shared.generated.resources.house_price_label
import housebuddy.shared.generated.resources.liquidity_info_main_agency
import housebuddy.shared.generated.resources.liquidity_info_main_deposit
import housebuddy.shared.generated.resources.liquidity_info_main_down_payment
import housebuddy.shared.generated.resources.liquidity_info_main_header
import housebuddy.shared.generated.resources.liquidity_info_mortgage_appraisal
import housebuddy.shared.generated.resources.liquidity_info_mortgage_application
import housebuddy.shared.generated.resources.liquidity_info_mortgage_fire_insurance
import housebuddy.shared.generated.resources.liquidity_info_mortgage_header
import housebuddy.shared.generated.resources.liquidity_info_mortgage_life_insurance
import housebuddy.shared.generated.resources.liquidity_info_mortgage_notary
import housebuddy.shared.generated.resources.liquidity_info_mortgage_substitute_tax
import housebuddy.shared.generated.resources.liquidity_info_notary_archive
import housebuddy.shared.generated.resources.liquidity_info_notary_cadastral
import housebuddy.shared.generated.resources.liquidity_info_notary_drafting
import housebuddy.shared.generated.resources.liquidity_info_notary_header
import housebuddy.shared.generated.resources.liquidity_info_notary_mortgage_registry
import housebuddy.shared.generated.resources.liquidity_info_notary_registration
import housebuddy.shared.generated.resources.liquidity_info_notary_regulatory
import housebuddy.shared.generated.resources.liquidity_info_notary_searches
import housebuddy.shared.generated.resources.liquidity_info_section_total
import housebuddy.shared.generated.resources.mortgage_payment_estimate_label
import housebuddy.shared.generated.resources.mortgage_request_label
import housebuddy.shared.generated.resources.mortgage_request_supporting
import housebuddy.shared.generated.resources.suffix_eur
import housebuddy.shared.generated.resources.suffix_percent
import housebuddy.shared.generated.resources.upfront_liquidity_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun HousePriceScreen(
    state: HousePriceViewState = HousePriceViewState(),
    result: HousePriceResult,
    onIntent: (HousePriceEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val numberOfBuyers = parsePositiveIntOrDefault(state.numberOfBuyersInput)
    val housePrice = parseInputOrDefault(state.housePriceInput, 150000.0)
    val mortgageRequestPercent = parseInputOrDefault(state.mortgageRequestInput, 80.0).coerceIn(70.0, 100.0)
    val mortgageAmount = housePrice * mortgageRequestPercent / 100.0
    val mortgageRequestSupportingText = stringResource(
        Res.string.mortgage_request_supporting,
        formatEuroAmount(mortgageAmount),
        formatNumber(mortgageRequestPercent, 0)
    )
    val expenseLabel = if (numberOfBuyers == 1) {
        stringResource(Res.string.expense_label_total)
    } else {
        stringResource(Res.string.expense_label_per_capita, numberOfBuyers)
    }
    val upfrontLiquidityLabel = stringResource(Res.string.upfront_liquidity_label)

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                StepperInputField(
                    label = stringResource(Res.string.house_price_label),
                    value = state.housePriceInput,
                    onValueChange = { onIntent(HousePriceEvent.HousePriceChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.HousePriceStepped(it)) },
                    suffix = stringResource(Res.string.suffix_eur),
                    topPadding = 0.dp
                )
                StepperInputField(
                    label = stringResource(Res.string.mortgage_request_label),
                    value = state.mortgageRequestInput,
                    onValueChange = { onIntent(HousePriceEvent.MortgageRequestChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.MortgageRequestStepped(it)) },
                    suffix = stringResource(Res.string.suffix_percent),
                    supportingText = mortgageRequestSupportingText
                )
                if (state.isAgencyCommissionPercentage) {
                    StepperInputField(
                        label = stringResource(Res.string.agency_percentage_label),
                        value = state.agencyPercentageInput,
                        onValueChange = { onIntent(HousePriceEvent.AgencyPercentageChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.AgencyPercentageStepped(it)) },
                        suffix = stringResource(Res.string.suffix_percent)
                    )
                } else {
                    StepperInputField(
                        label = stringResource(Res.string.agency_fixed_label),
                        value = state.agencyFixedFeeInput,
                        onValueChange = { onIntent(HousePriceEvent.AgencyFixedFeeChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.AgencyFixedFeeStepped(it)) },
                        suffix = stringResource(Res.string.suffix_eur)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = upfrontLiquidityLabel, fontWeight = FontWeight.Bold)
                    InfoIcon(
                        infoText = buildLiquidityInfoText(result),
                        dialogTitle = upfrontLiquidityLabel,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultFieldCompact(
                        label = expenseLabel,
                        value = result.upfrontCashNeeded / numberOfBuyers,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),) {
                    Text(
                        text = stringResource(Res.string.mortgage_payment_estimate_label),
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultFieldCompact(
                        label = expenseLabel,
                        value = result.monthlyMortgagePayment / numberOfBuyers,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
    }
}

@Composable
private fun buildLiquidityInfoText(result: HousePriceResult): String {
    val b = result.breakdown
    val totalMainExpenses = b.deposit + b.downPaymentMinusDeposit + b.agencyFeeWithVat
    val totalMortgageCosts = b.applicationFee + b.substituteTax + b.appraisal +
        b.mandatoryFireInsurance + b.lifeInsurance + b.mortgageNotaryFee
    val totalPurchaseNotaryCosts = b.registrationTax + b.mortgageRegistryTax + b.cadastralTax +
        b.archiveFee + b.notaryDraftingAndCopyFees +
        b.notaryRegulatoryContributions + b.mortgageRegistrySearches

    return buildString {
        appendLine(stringResource(Res.string.liquidity_info_main_header))
        appendLine(stringResource(Res.string.liquidity_info_main_deposit, formatEuroAmount(b.deposit)))
        appendLine(stringResource(Res.string.liquidity_info_main_down_payment, formatEuroAmount(b.downPaymentMinusDeposit)))
        appendLine(stringResource(Res.string.liquidity_info_main_agency, formatEuroAmount(b.agencyFeeWithVat)))
        appendLine(stringResource(Res.string.liquidity_info_section_total, formatEuroAmount(totalMainExpenses)))
        appendLine()
        appendLine(stringResource(Res.string.liquidity_info_mortgage_header))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_application, formatEuroAmount(b.applicationFee)))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_substitute_tax, formatEuroAmount(b.substituteTax)))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_appraisal, formatEuroAmount(b.appraisal)))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_fire_insurance, formatEuroAmount(b.mandatoryFireInsurance)))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_life_insurance, formatEuroAmount(b.lifeInsurance)))
        appendLine(stringResource(Res.string.liquidity_info_mortgage_notary, formatEuroAmount(b.mortgageNotaryFee)))
        appendLine(stringResource(Res.string.liquidity_info_section_total, formatEuroAmount(totalMortgageCosts)))
        appendLine()
        appendLine(stringResource(Res.string.liquidity_info_notary_header))
        appendLine(stringResource(Res.string.liquidity_info_notary_registration, formatEuroAmount(b.registrationTax)))
        appendLine(stringResource(Res.string.liquidity_info_notary_mortgage_registry, formatEuroAmount(b.mortgageRegistryTax)))
        appendLine(stringResource(Res.string.liquidity_info_notary_cadastral, formatEuroAmount(b.cadastralTax)))
        appendLine(stringResource(Res.string.liquidity_info_notary_archive, formatEuroAmount(b.archiveFee)))
        appendLine(stringResource(Res.string.liquidity_info_notary_drafting, formatEuroAmount(b.notaryDraftingAndCopyFees)))
        appendLine(stringResource(Res.string.liquidity_info_notary_regulatory, formatEuroAmount(b.notaryRegulatoryContributions)))
        appendLine(stringResource(Res.string.liquidity_info_notary_searches, formatEuroAmount(b.mortgageRegistrySearches)))
        append(stringResource(Res.string.liquidity_info_section_total, formatEuroAmount(totalPurchaseNotaryCosts)))
    }
}
