package com.example.housebuddy.data.remote.logging

import io.ktor.client.plugins.logging.Logger

internal object HttpTraceLogger : Logger {
    override fun log(message: String) {
        traceHttpLog(message)
    }
}

internal expect fun traceHttpLog(message: String)
