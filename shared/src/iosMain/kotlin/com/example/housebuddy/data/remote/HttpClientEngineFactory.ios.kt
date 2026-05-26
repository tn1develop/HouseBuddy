package com.example.housebuddy.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

internal actual fun createHttpClientEngine(): HttpClientEngine = Darwin.create()
