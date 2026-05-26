package com.example.housebuddy.data.remote

import com.example.housebuddy.data.remote.logging.HttpTraceLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging

internal fun createHttpClient(): HttpClient = HttpClient(createHttpClientEngine()) {
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 60_000
    }
    install(Logging) {
        logger = HttpTraceLogger
        level = LogLevel.ALL
    }
}
