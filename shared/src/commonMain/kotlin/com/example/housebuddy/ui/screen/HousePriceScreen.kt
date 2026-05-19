package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.usecase.CalculateHousePriceUseCase
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.example.housebuddy.ui.components.ResultField
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(modifier = Modifier.padding(bottom = 32.dp), onClick = { onIntent(HousePriceEvent.ToggleAdvancedFields) }) {
                Text(if (state.showAdvancedFields) "-" else "+")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
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
                    suffix = "EUR"
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

                if (state.showAdvancedFields) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Mutuo Green (classe A o B)")
                        Switch(
                            checked = state.mutuoGreen,
                            onCheckedChange = { onIntent(HousePriceEvent.MutuoGreenChanged(it)) }
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Percentuale o fisso (Agenzia)")
                        Switch(
                            checked = state.isPercentuale,
                            onCheckedChange = { onIntent(HousePriceEvent.IsPercentualeChanged(it)) }
                        )
                    }
                    StepperInputField(
                        label = "Tasso mutuo",
                        value = state.tassoMutuoInput,
                        onValueChange = { onIntent(HousePriceEvent.TassoMutuoChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.TassoMutuoStepped(it)) },
                        suffix = "%"
                    )
                    StepperInputField(
                        label = "Anni mutuo",
                        value = state.anniMutuoInput,
                        onValueChange = { onIntent(HousePriceEvent.AnniMutuoChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.AnniMutuoStepped(it)) },
                        suffix = "anni"
                    )
                    StepperInputField(
                        label = "Rendita catastale",
                        value = state.renditaCatastaleInput,
                        onValueChange = { onIntent(HousePriceEvent.RenditaCatastaleChanged(it)) },
                        onStep = { onIntent(HousePriceEvent.RenditaCatastaleStepped(it)) },
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
                if (state.showAdvancedFields) {
                    ResultField(label = "Soldi da chiedere in mutuo", value = result.soldiMutuo)
                    ResultField(label = "Costo totale casa (a fine mutuo)", value = result.costoTotaleCasa)
                }
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Totale o Pro Capite")
                    Switch(
                        checked = state.isTotalExpenses,
                        onCheckedChange = { onIntent(HousePriceEvent.IsProCapiteChanged(it)) }
                    )
                }
            }
        }
    }
}

private fun HousePriceViewState.toInput() = HousePriceInput(
    prezzoCasaInput = prezzoCasaInput,
    anticipoInput = anticipoInput,
    percentualeAgenziaInput = percentualeAgenziaInput,
    fissoAgenziaInput = fissoAgenziaInput,
    isPercentuale = isPercentuale,
    tassoMutuoInput = tassoMutuoInput,
    anniMutuoInput = anniMutuoInput,
    renditaCatastaleInput = renditaCatastaleInput
)

private fun buildInfoText(result: HousePriceResult): String {
    return "Anticipo, caparra, agenzia con Iva, \n\nmutuo:  \n" +
        "- istruttoria ${result.breakdown.istruttoria}\n" +
        "- imposta sostitutiva ${result.breakdown.impostaSostitutiva}\n" +
        "- perizia ${result.breakdown.perizia}\n " +
        "- assicurazione incendio ${result.breakdown.polizzaIncendioObbligatoria}\n " +
        "- assicurazione vita ${result.breakdown.polizzaVita}\n\n " +
        "notaio: \n" +
        "- Imposta di registro ${result.breakdown.impostaRegistro}\n" +
        "- Imposta ipotecaria ${result.breakdown.impostaIpotecaria}\n" +
        "- Imposta catastale ${result.breakdown.impostaCatastale}\n" +
        "- Tassa archivio ${result.breakdown.tassaArchivio}\n" +
        "- Onorario, scritturazione, diritti di copia ${result.breakdown.onorarioScritturazioneDirittiCopia}\n" +
        "- Contributo CNN, Consiglio, Cassa, Iscrizione a repertorio ${result.breakdown.contributoCNNConsiglioCassaIscrizioneRepertorio}\n" +
        "- Visure ipotecarie ${result.breakdown.visureIpotecarie}"
}
