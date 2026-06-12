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
    val numeroCompratori = parsePositiveIntOrDefault(state.numeroCompratoriInput)
    val prezzoCasa = parseInputOrDefault(state.prezzoCasaInput, 150000.0)
    val richiestaMutuo = parseInputOrDefault(state.richiestaMutuoInput, 80.0).coerceIn(70.0, 100.0)
    val importoMutuo = prezzoCasa * richiestaMutuo / 100.0
    val richiestaMutuoSupportingText =
        "${formatEuroAmount(importoMutuo)} · ${formatNumber(richiestaMutuo, 0)}% del prezzo casa"
    val expenseLabel = if (numeroCompratori == 1) {
        "Totale"
    } else {
        "Pro capite ($numeroCompratori pers.)"
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
                    value = state.prezzoCasaInput,
                    onValueChange = { onIntent(HousePriceEvent.PrezzoCasaChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.PrezzoCasaStepped(it)) },
                    suffix = "EUR",
                    topPadding = 0.dp
                )
                StepperInputField(
                    label = "Richiesta di mutuo",
                    value = state.richiestaMutuoInput,
                    onValueChange = { onIntent(HousePriceEvent.RichiestaMutuoChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.RichiestaMutuoStepped(it)) },
                    suffix = "%",
                    supportingText = richiestaMutuoSupportingText
                )
                if (state.isPercentuale) {
                    StepperInputField(
                        label = "Percentuale agenzia",
                        value = state.percentualeAgenziaInput,
                        onValueChange = { onIntent(HousePriceEvent.PercentualeAgenziaChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.PercentualeAgenziaStepped(it)) },
                        suffix = "%"
                    )
                } else {
                    StepperInputField(
                        label = "Fisso agenzia",
                        value = state.fissoAgenziaInput,
                        onValueChange = { onIntent(HousePriceEvent.FissoAgenziaChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.FissoAgenziaStepped(it)) },
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

                //ResultFieldCompact(label = "Soldi da chiedere in mutuo", value = result.soldiMutuo)
                //ResultFieldCompact(label = "Costo totale casa (a fine mutuo)", value = result.costoTotaleCasa)

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
                        value = result.soldiSubito / numeroCompratori,
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
                        value = result.rataMutuo / numeroCompratori,
                        modifier = Modifier.weight(1f)
                    )
                }

            }
    }
}

private fun buildInfoText(result: HousePriceResult): String {
    val b = result.breakdown
    val totSpesePrincipali = b.caparra + b.anticipoToltaCaparra + b.agenziaConIva
    val totMutuo = b.istruttoria + b.impostaSostitutiva + b.perizia +
        b.polizzaIncendioObbligatoria + b.polizzaVita + b.notaioMutuo
    val totNotaioAcquisto = b.impostaRegistro + b.impostaIpotecaria + b.impostaCatastale +
        b.tassaArchivio + b.onorarioScritturazioneDirittiCopia +
        b.contributoCNNConsiglioCassaIscrizioneRepertorio + b.visureIpotecarie

    return "spese principali:\n" +
        "- Caparra ${formatEuroAmount(b.caparra)}\n" +
        "- Anticipo tolta la caparra ${formatEuroAmount(b.anticipoToltaCaparra)}\n" +
        "- Agenzia con Iva ${formatEuroAmount(b.agenziaConIva)}\n" +
        "TOT: ${formatEuroAmount(totSpesePrincipali)}\n\n" +
        "mutuo:\n" +
        "- istruttoria ${formatEuroAmount(b.istruttoria)}\n" +
        "- imposta sostitutiva ${formatEuroAmount(b.impostaSostitutiva)}\n" +
        "- perizia ${formatEuroAmount(b.perizia)}\n" +
        "- assicurazione incendio ${formatEuroAmount(b.polizzaIncendioObbligatoria)}\n" +
        "- assicurazione vita ${formatEuroAmount(b.polizzaVita)}\n" +
        "- notaio mutuo ${formatEuroAmount(b.notaioMutuo)}\n" +
        "TOT: ${formatEuroAmount(totMutuo)}\n\n" +
        "notaio acquisto:\n" +
        "- Imposta di registro ${formatEuroAmount(b.impostaRegistro)}\n" +
        "- Imposta ipotecaria ${formatEuroAmount(b.impostaIpotecaria)}\n" +
        "- Imposta catastale ${formatEuroAmount(b.impostaCatastale)}\n" +
        "- Tassa archivio ${formatEuroAmount(b.tassaArchivio)}\n" +
        "- Onorario, scritturazione, diritti di copia ${formatEuroAmount(b.onorarioScritturazioneDirittiCopia)}\n" +
        "- Contributo CNN, Consiglio, Cassa, Iscrizione a repertorio ${formatEuroAmount(b.contributoCNNConsiglioCassaIscrizioneRepertorio)}\n" +
        "- Visure ipotecarie ${formatEuroAmount(b.visureIpotecarie)}\n" +
        "TOT: ${formatEuroAmount(totNotaioAcquisto)}"
}
