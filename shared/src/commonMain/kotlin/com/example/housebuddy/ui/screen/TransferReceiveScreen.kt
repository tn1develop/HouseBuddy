package com.example.housebuddy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.transfer.TransferMode

@Composable
fun ReceiveTransferScreen(
    onChangeDirection: () -> Unit,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BluetoothConnectionScreen(
        title = "Ricevi trasferimento",
        subtitle = "Questo telefono cerca via Bluetooth un dispositivo in modalità invio.",
        waitingForScreen = "Invia Trasferimento",
        mode = TransferMode.Receive,
        onChangeDirection = onChangeDirection,
        onTransferCompleted = { onIntent(HousePriceEvent.ReloadFromStorage) },
        modifier = modifier
    )
}
