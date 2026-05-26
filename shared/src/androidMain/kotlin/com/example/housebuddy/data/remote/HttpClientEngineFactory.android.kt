package com.example.housebuddy.data.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun createHttpClientEngine(): HttpClientEngine = OkHttp.create()
