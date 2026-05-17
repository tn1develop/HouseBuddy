package com.example.housebuddy.domain.usecase

import com.example.housebuddy.domain.model.HousePriceBreakdown
import com.example.housebuddy.domain.model.HousePriceInput
import com.example.housebuddy.domain.model.HousePriceResult
import com.example.housebuddy.domain.util.calcolaRataMutuo
import com.example.housebuddy.domain.util.parseInputOrDefault

class CalculateHousePriceUseCase {
    operator fun invoke(input: HousePriceInput): HousePriceResult {
        val prezzoCasa = parseInputOrDefault(input.prezzoCasaInput, 150000.0).coerceIn(80000.0, 200000.0)
        val anticipo = parseInputOrDefault(input.anticipoInput, 20.0).coerceIn(0.0, 30.0)
        val percentualeAgenzia = parseInputOrDefault(input.percentualeAgenziaInput, 4.0).coerceIn(0.0, 5.0)
        val fissoAgenzia = parseInputOrDefault(input.fissoAgenziaInput, 7000.0).coerceIn(0.0, 10000.0)
        val tassoMutuo = parseInputOrDefault(input.tassoMutuoInput, 2.99).coerceIn(1.0, 5.0)
        val anniMutuo = parseInputOrDefault(input.anniMutuoInput, 30.0).toInt().coerceIn(5, 40)
        val renditaCatastale = parseInputOrDefault(input.renditaCatastaleInput, 1071.0).coerceIn(0.0, 4000.0)

        val impostaRegistro = renditaCatastale * 1.05 * 110 * 0.02
        val impostaIpotecaria = 50.0
        val impostaCatastale = 50.0
        val tassaArchivio = 30.4
        val onorarioScritturazioneDirittiCopia = 1206.73
        val contributoCNNConsiglioCassaIscrizioneRepertorio = 145.4
        val visureIpotecarie = 200.0
        val imposteENotaio = impostaRegistro + impostaIpotecaria + impostaCatastale + tassaArchivio +
            onorarioScritturazioneDirittiCopia + contributoCNNConsiglioCassaIscrizioneRepertorio + visureIpotecarie
        val istruttoria = 1000.0
        val impostaSostitutiva = 300.0
        val perizia = 300.0
        val polizzaIncendioObbligatoria = 650.0
        val polizzaVita = 150.0
        val speseAvvioMutuo = istruttoria + impostaSostitutiva + perizia + polizzaIncendioObbligatoria + polizzaVita
        val quotaAgenzia = if (input.isPercentuale) prezzoCasa * (percentualeAgenzia / 100.0) else fissoAgenzia
        val soldiSubito = (quotaAgenzia * 1.22) + imposteENotaio + speseAvvioMutuo + prezzoCasa * (anticipo / 100.0)
        val soldiMutuo = prezzoCasa * ((100.0 - anticipo) / 100.0)
        val rataMutuo = calcolaRataMutuo(soldiMutuo, tassoAnnuo = tassoMutuo, durataAnni = anniMutuo)
        val costoTotaleCasa = soldiSubito + (rataMutuo * 12.0 * anniMutuo)

        return HousePriceResult(
            soldiSubito = soldiSubito,
            soldiMutuo = soldiMutuo,
            rataMutuo = rataMutuo,
            costoTotaleCasa = costoTotaleCasa,
            breakdown = HousePriceBreakdown(
                impostaRegistro = impostaRegistro,
                impostaIpotecaria = impostaIpotecaria,
                impostaCatastale = impostaCatastale,
                tassaArchivio = tassaArchivio,
                onorarioScritturazioneDirittiCopia = onorarioScritturazioneDirittiCopia,
                contributoCNNConsiglioCassaIscrizioneRepertorio = contributoCNNConsiglioCassaIscrizioneRepertorio,
                visureIpotecarie = visureIpotecarie,
                istruttoria = istruttoria,
                impostaSostitutiva = impostaSostitutiva,
                perizia = perizia,
                polizzaIncendioObbligatoria = polizzaIncendioObbligatoria,
                polizzaVita = polizzaVita
            )
        )
    }
}
