package com.example.housebuddy.data.remote.logging

import platform.Foundation.NSLog

private const val HTTP_LOG_TAG = "HouseBuddyHttp"

internal actual fun traceHttpLog(message: String) {
    NSLog("$HTTP_LOG_TAG: $message")
}
