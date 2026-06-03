package com.example.housebuddy.data.local

import com.example.housebuddy.domain.util.formatNumber
import com.example.housebuddy.domain.util.parseInputOrDefault
import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.russhwolf.settings.Settings

class HousePriceStateStorage(
    private val settings: Settings = createHousePriceSettings()
) {
    fun load(): HousePriceViewState {
        val defaults = HousePriceViewState()
        return HousePriceViewState(
            prezzoCasaInput = settings.getString(
                HousePriceSettingsKeys.PREZZO_CASA_INPUT,
                defaults.prezzoCasaInput
            ),
            richiestaMutuoInput = settings.getString(
                HousePriceSettingsKeys.RICHIESTA_MUTUO_INPUT,
                defaults.richiestaMutuoInput
            ),
            caparraInput = settings.getString(
                HousePriceSettingsKeys.CAPARRA_INPUT,
                defaults.caparraInput
            ),
            percentualeAgenziaInput = settings.getString(
                HousePriceSettingsKeys.PERCENTUALE_AGENZIA_INPUT,
                defaults.percentualeAgenziaInput
            ),
            fissoAgenziaInput = settings.getString(
                HousePriceSettingsKeys.FISSO_AGENZIA_INPUT,
                defaults.fissoAgenziaInput
            ),
            mutuoGreen = settings.getBoolean(
                HousePriceSettingsKeys.MUTUO_GREEN,
                defaults.mutuoGreen
            ),
            isPercentuale = settings.getBoolean(
                HousePriceSettingsKeys.IS_PERCENTUALE,
                defaults.isPercentuale
            ),
            numeroCompratoriInput = settings.getString(
                HousePriceSettingsKeys.NUMERO_COMPRATORI_INPUT,
                defaults.numeroCompratoriInput
            ),
            tassoMutuoInput = settings.getString(
                HousePriceSettingsKeys.TASSO_MUTUO_INPUT,
                defaults.tassoMutuoInput
            ),
            anniMutuoInput = settings.getString(
                HousePriceSettingsKeys.ANNI_MUTUO_INPUT,
                defaults.anniMutuoInput
            ),
            renditaCatastaleInput = settings.getString(
                HousePriceSettingsKeys.RENDITA_CATASTALE_INPUT,
                defaults.renditaCatastaleInput
            ),
            canoneAffittoInput = settings.getString(
                HousePriceSettingsKeys.CANONE_AFFITTO_INPUT,
                defaults.canoneAffittoInput
            ),
            liquiditaAttualeInput = settings.getString(
                HousePriceSettingsKeys.LIQUIDITA_ATTUALE_INPUT,
                defaults.liquiditaAttualeInput
            ),
            risparmioAnnualeInput = settings.getString(
                HousePriceSettingsKeys.RISPARMIO_ANNUALE_INPUT,
                defaults.risparmioAnnualeInput
            )
        )
    }

    fun save(state: HousePriceViewState) {
        settings.putString(HousePriceSettingsKeys.PREZZO_CASA_INPUT, state.prezzoCasaInput)
        settings.putString(HousePriceSettingsKeys.RICHIESTA_MUTUO_INPUT, state.richiestaMutuoInput)
        settings.putString(HousePriceSettingsKeys.CAPARRA_INPUT, state.caparraInput)
        settings.putString(HousePriceSettingsKeys.PERCENTUALE_AGENZIA_INPUT, state.percentualeAgenziaInput)
        settings.putString(HousePriceSettingsKeys.FISSO_AGENZIA_INPUT, state.fissoAgenziaInput)
        settings.putBoolean(HousePriceSettingsKeys.MUTUO_GREEN, state.mutuoGreen)
        settings.putBoolean(HousePriceSettingsKeys.IS_PERCENTUALE, state.isPercentuale)
        settings.putString(HousePriceSettingsKeys.NUMERO_COMPRATORI_INPUT, state.numeroCompratoriInput)
        settings.putString(HousePriceSettingsKeys.TASSO_MUTUO_INPUT, state.tassoMutuoInput)
        settings.putString(HousePriceSettingsKeys.ANNI_MUTUO_INPUT, state.anniMutuoInput)
        settings.putString(HousePriceSettingsKeys.RENDITA_CATASTALE_INPUT, state.renditaCatastaleInput)
        settings.putString(HousePriceSettingsKeys.CANONE_AFFITTO_INPUT, state.canoneAffittoInput)
        settings.putString(HousePriceSettingsKeys.LIQUIDITA_ATTUALE_INPUT, state.liquiditaAttualeInput)
        settings.putString(HousePriceSettingsKeys.RISPARMIO_ANNUALE_INPUT, state.risparmioAnnualeInput)
    }
}
