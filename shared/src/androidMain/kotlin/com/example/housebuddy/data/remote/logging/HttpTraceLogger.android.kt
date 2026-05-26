package com.example.housebuddy.data.remote.logging

import android.util.Log

private const val HTTP_LOG_TAG = "HouseBuddyHttp"

internal actual fun traceHttpLog(message: String) {
    Log.d(HTTP_LOG_TAG, message)
}
