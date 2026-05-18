package com.example.housebuddy.data.local

import com.example.housebuddy.presentation.mvi.HousePriceViewState
import com.russhwolf.settings.Settings

class HousePriceStateStorage(
    private val settings: Settings = createHousePriceSettings()
) {
    fun load(): HousePriceViewState {
        val defaults = HousePriceViewState()
        return HousePriceViewState(
            showAdvancedFields = settings.getBoolean(
                HousePriceSettingsKeys.SHOW_ADVANCED_FIELDS,
                defaults.showAdvancedFields
            ),
            prezzoCasaInput = settings.getString(
                HousePriceSettingsKeys.PREZZO_CASA_INPUT,
                defaults.prezzoCasaInput
            ),
            anticipoInput = settings.getString(
                HousePriceSettingsKeys.ANTICIPO_INPUT,
                defaults.anticipoInput
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
            )
        )
    }

    fun save(state: HousePriceViewState) {
        settings.putBoolean(HousePriceSettingsKeys.SHOW_ADVANCED_FIELDS, state.showAdvancedFields)
        settings.putString(HousePriceSettingsKeys.PREZZO_CASA_INPUT, state.prezzoCasaInput)
        settings.putString(HousePriceSettingsKeys.ANTICIPO_INPUT, state.anticipoInput)
        settings.putString(HousePriceSettingsKeys.PERCENTUALE_AGENZIA_INPUT, state.percentualeAgenziaInput)
        settings.putString(HousePriceSettingsKeys.FISSO_AGENZIA_INPUT, state.fissoAgenziaInput)
        settings.putBoolean(HousePriceSettingsKeys.MUTUO_GREEN, state.mutuoGreen)
        settings.putBoolean(HousePriceSettingsKeys.IS_PERCENTUALE, state.isPercentuale)
        settings.putString(HousePriceSettingsKeys.TASSO_MUTUO_INPUT, state.tassoMutuoInput)
        settings.putString(HousePriceSettingsKeys.ANNI_MUTUO_INPUT, state.anniMutuoInput)
        settings.putString(HousePriceSettingsKeys.RENDITA_CATASTALE_INPUT, state.renditaCatastaleInput)
    }
}
