package com.example.housebuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.housebuddy.data.local.initHousePriceSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initHousePriceSettings(this)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}
