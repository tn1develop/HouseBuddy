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
import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.util.formatEuroAmount
import com.example.housebuddy.domain.usecase.CalculateHousePriceUseCase
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.InfoIcon
import com.example.housebuddy.ui.components.ResultFieldCompact
import com.example.housebuddy.ui.components.StepperInputField

@Composable
fun HousePriceScreen(
    state: HousePriceViewState = HousePriceViewState(),
    result: HousePriceResult = CalculateHousePriceUseCase().invoke(state.toInput()),
    onIntent: (HousePriceEvent) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                    label = "Anticipo",
                    value = state.anticipoInput,
                    onValueChange = { onIntent(HousePriceEvent.AnticipoChanged(it)) },
                    onStep = { onIntent(HousePriceEvent.AnticipoStepped(it)) },
                    suffix = "%"
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
                    if(state.isTotalExpenses) {
                        ResultFieldCompact(
                            label = "Totale",
                            value = result.soldiSubito,
                            modifier = Modifier.weight(1f)
                        )
                    }else {
                        ResultFieldCompact(
                            label = "Pro capite",
                            value = result.soldiSubito / 2.0,
                            modifier = Modifier.weight(1f)
                        )
                    }
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
                    if(state.isTotalExpenses) {
                        ResultFieldCompact(
                            label = "Totale",
                            value = result.rataMutuo,
                            modifier = Modifier.weight(1f)
                        )
                    }else {
                        ResultFieldCompact(
                            label = "Pro capite",
                            value = result.rataMutuo / 2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

            }
    }
}

private fun HousePriceViewState.toInput() = HousePriceInput(
    prezzoCasaInput = prezzoCasaInput,
    anticipoInput = anticipoInput,
    caparraInput = caparraInput,
    percentualeAgenziaInput = percentualeAgenziaInput,
    fissoAgenziaInput = fissoAgenziaInput,
    isPercentuale = isPercentuale,
    tassoMutuoInput = tassoMutuoInput,
    anniMutuoInput = anniMutuoInput,
    renditaCatastaleInput = renditaCatastaleInput
)

private fun buildInfoText(result: HousePriceResult): String {
    return  "spese principali:\n" +
        "- Caparra ${formatEuroAmount(result.breakdown.caparra)}\n" +
        "- Anticipo tolta la caparra ${formatEuroAmount(result.breakdown.anticipoToltaCaparra)}\n" +
        "- Agenzia con Iva ${formatEuroAmount(result.breakdown.agenziaConIva)}\n\n" +
            "mutuo:  \n" +
        "- istruttoria ${formatEuroAmount(result.breakdown.istruttoria)}\n" +
        "- imposta sostitutiva ${formatEuroAmount(result.breakdown.impostaSostitutiva)}\n" +
        "- perizia ${formatEuroAmount(result.breakdown.perizia)}\n " +
        "- assicurazione incendio ${formatEuroAmount(result.breakdown.polizzaIncendioObbligatoria)}\n " +
        "- assicurazione vita ${formatEuroAmount(result.breakdown.polizzaVita)}\n\n " +
            "notaio: \n" +
        "- Imposta di registro ${formatEuroAmount(result.breakdown.impostaRegistro)}\n" +
        "- Imposta ipotecaria ${formatEuroAmount(result.breakdown.impostaIpotecaria)}\n" +
        "- Imposta catastale ${formatEuroAmount(result.breakdown.impostaCatastale)}\n" +
        "- Tassa archivio ${formatEuroAmount(result.breakdown.tassaArchivio)}\n" +
        "- Onorario, scritturazione, diritti di copia ${formatEuroAmount(result.breakdown.onorarioScritturazioneDirittiCopia)}\n" +
        "- Contributo CNN, Consiglio, Cassa, Iscrizione a repertorio ${formatEuroAmount(result.breakdown.contributoCNNConsiglioCassaIscrizioneRepertorio)}\n" +
        "- Visure ipotecarie ${formatEuroAmount(result.breakdown.visureIpotecarie)}"
}
