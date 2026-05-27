package com.example.housebuddy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.housebuddy.transfer.TransferMode

@Composable
fun SendTransferScreen(
    onChangeDirection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BluetoothConnectionScreen(
        title = "Invia trasferimento",
        subtitle = "Questo telefono cerca via Bluetooth un dispositivo in modalità ricezione.",
        waitingForScreen = "Ricevi Trasferimento",
        mode = TransferMode.Send,
        onChangeDirection = onChangeDirection,
        modifier = modifier
    )
}