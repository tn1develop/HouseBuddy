package com.example.housebuddy.data.repository

import com.example.housebuddy.data.remote.SdmxGenericDataParser
import com.example.housebuddy.data.remote.createHttpClient
import com.example.housebuddy.data.remote.logging.traceHttpLog
import com.example.housebuddy.domain.model.ExchangeRateObservation
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EcbExchangeRateRepository(
    private val httpClient: HttpClient = createHttpClient()
) {
    suspend fun fetchEuriborObservations(): List<ExchangeRateObservation> = withContext(Dispatchers.Default) {
        val response = httpClient.get(ECB_EURIBOR_URL) {
            accept(ContentType.Application.Xml)
        }
        val xml = response.bodyAsText()
        traceHttpLog("ECB response body: ${xml.length} chars")

        val observations = SdmxGenericDataParser.parseObservations(xml)
        traceHttpLog("ECB parsed ${observations.size} observations")
        observations
    }

    companion object {
        const val ECB_EURIBOR_URL =
            "https://data-api.ecb.europa.eu/service/data/FM/M.U2.EUR.RT.MM.EURIBOR3MD_.HSTA"
    }
}
