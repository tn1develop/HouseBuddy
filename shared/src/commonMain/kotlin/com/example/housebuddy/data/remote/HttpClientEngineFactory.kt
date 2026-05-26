package com.example.housebuddy.data.remote

import io.ktor.client.engine.HttpClientEngine

internal expect fun createHttpClientEngine(): HttpClientEngine
