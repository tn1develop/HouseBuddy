package com.example.housebuddy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform