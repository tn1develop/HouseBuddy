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
    val mortgageRequestSupportingText =
        "${formatEuroAmount(mortgageAmount)} · ${formatNumber(mortgageRequestPercent, 0)}% del prezzo casa"
    val expenseLabel = if (numberOfBuyers == 1) {
        "Totale"
    } else {
        "Pro capite ($numberOfBuyers pers.)"
    }

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
                    label = "Prezzo casa",
                    value = state.housePriceInput,
                    onValueChange = { onIntent(HousePriceEvent.HousePriceChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.HousePriceStepped(it)) },
                    suffix = "EUR",
                    topPadding = 0.dp
                )
                StepperInputField(
                    label = "Richiesta di mutuo",
                    value = state.mortgageRequestInput,
                    onValueChange = { onIntent(HousePriceEvent.MortgageRequestChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.MortgageRequestStepped(it)) },
                    suffix = "%",
                    supportingText = mortgageRequestSupportingText
                )
                if (state.isAgencyCommissionPercentage) {
                    StepperInputField(
                        label = "Percentuale agenzia",
                        value = state.agencyPercentageInput,
                        onValueChange = { onIntent(HousePriceEvent.AgencyPercentageChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.AgencyPercentageStepped(it)) },
                        suffix = "%"
                    )
                } else {
                    StepperInputField(
                        label = "Fisso agenzia",
                        value = state.agencyFixedFeeInput,
                        onValueChange = { onIntent(HousePriceEvent.AgencyFixedFeeChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.AgencyFixedFeeStepped(it)) },
                        suffix = "EUR"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

                //ResultFieldCompact(label = "Soldi da chiedere in mutuo", value = result.mortgageLoanAmount)
                //ResultFieldCompact(label = "Costo totale casa (a fine mutuo)", value = result.totalHouseCost)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Liquidità necessaria all'atto o prima", fontWeight = FontWeight.Bold)
                    InfoIcon(
                        infoText = buildInfoText(result),
                        dialogTitle = "Liquidità necessaria all'atto o prima",
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
                    Text(text = "Ipotesi rata mutuo", fontWeight = FontWeight.Bold)
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

private fun buildInfoText(result: HousePriceResult): String {
    val b = result.breakdown
    val totalMainExpenses = b.deposit + b.downPaymentMinusDeposit + b.agencyFeeWithVat
    val totalMortgageCosts = b.applicationFee + b.substituteTax + b.appraisal +
        b.mandatoryFireInsurance + b.lifeInsurance + b.mortgageNotaryFee
    val totalPurchaseNotaryCosts = b.registrationTax + b.mortgageRegistryTax + b.cadastralTax +
        b.archiveFee + b.notaryDraftingAndCopyFees +
        b.notaryRegulatoryContributions + b.mortgageRegistrySearches

    return "spese principali:\n" +
        "- Caparra ${formatEuroAmount(b.deposit)}\n" +
        "- Anticipo tolta la caparra ${formatEuroAmount(b.downPaymentMinusDeposit)}\n" +
        "- Agenzia con Iva ${formatEuroAmount(b.agencyFeeWithVat)}\n" +
        "TOT: ${formatEuroAmount(totalMainExpenses)}\n\n" +
        "mutuo:\n" +
        "- istruttoria ${formatEuroAmount(b.applicationFee)}\n" +
        "- imposta sostitutiva ${formatEuroAmount(b.substituteTax)}\n" +
        "- perizia ${formatEuroAmount(b.appraisal)}\n" +
        "- assicurazione incendio ${formatEuroAmount(b.mandatoryFireInsurance)}\n" +
        "- assicurazione vita ${formatEuroAmount(b.lifeInsurance)}\n" +
        "- notaio mutuo ${formatEuroAmount(b.mortgageNotaryFee)}\n" +
        "TOT: ${formatEuroAmount(totalMortgageCosts)}\n\n" +
        "notaio acquisto:\n" +
        "- Imposta di registro ${formatEuroAmount(b.registrationTax)}\n" +
        "- Imposta ipotecaria ${formatEuroAmount(b.mortgageRegistryTax)}\n" +
        "- Imposta catastale ${formatEuroAmount(b.cadastralTax)}\n" +
        "- Tassa archivio ${formatEuroAmount(b.archiveFee)}\n" +
        "- Onorario, scritturazione, diritti di copia ${formatEuroAmount(b.notaryDraftingAndCopyFees)}\n" +
        "- Contributo CNN, Consiglio, Cassa, Iscrizione a repertorio ${formatEuroAmount(b.notaryRegulatoryContributions)}\n" +
        "- Visure ipotecarie ${formatEuroAmount(b.mortgageRegistrySearches)}\n" +
        "TOT: ${formatEuroAmount(totalPurchaseNotaryCosts)}"
}
