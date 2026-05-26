package com.example.housebuddy.domain.model

data class HousePriceInput(
    val prezzoCasaInput: String,
    val richiestaMutuoInput: String,
    val caparraInput: String,
    val percentualeAgenziaInput: String,
    val fissoAgenziaInput: String,
    val isPercentuale: Boolean,
    val tassoMutuoInput: String,
    val anniMutuoInput: String,
    val renditaCatastaleInput: String
)

data class HousePriceBreakdown(
    val caparra: Double,
    val anticipoToltaCaparra: Double,
    val agenziaConIva: Double,
    val impostaRegistro: Double,
    val impostaIpotecaria: Double,
    val impostaCatastale: Double,
    val tassaArchivio: Double,
    val onorarioScritturazioneDirittiCopia: Double,
    val contributoCNNConsiglioCassaIscrizioneRepertorio: Double,
    val visureIpotecarie: Double,
    val istruttoria: Double,
    val impostaSostitutiva: Double,
    val perizia: Double,
    val polizzaIncendioObbligatoria: Double,
    val polizzaVita: Double,
    val notaioMutuo: Double
)

data class HousePriceResult(
    val soldiSubito: Double,
    val soldiMutuo: Double,
    val rataMutuo: Double,
    val costoTotaleCasa: Double,
    val breakdown: HousePriceBreakdown
)
