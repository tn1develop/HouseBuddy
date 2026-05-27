package com.example.housebuddy.transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
actual fun rememberBluetoothTransferController(mode: TransferMode): BluetoothTransferController {
    return remember(mode) { IosBluetoothTransferController() }
}

private class IosBluetoothTransferController : BluetoothTransferController {
    private val mutableUiState = MutableStateFlow(
        BluetoothTransferUiState(
            supported = false,
            inProgress = false,
            statusMessage = "Trasferimento Bluetooth non ancora disponibile su iOS."
        )
    )

    override val uiState: StateFlow<BluetoothTransferUiState> = mutableUiState

    override fun start() = Unit
    override fun retry() = Unit
    override fun selectPeer(address: String) = Unit
    override fun stop() = Unit
}
