package com.example.housebuddy

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.housebuddy.data.local.initHousePriceSettings

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    initHousePriceSettings(LocalContext.current)
    App()
}
