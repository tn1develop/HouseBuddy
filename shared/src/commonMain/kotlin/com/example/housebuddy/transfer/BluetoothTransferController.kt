package com.example.housebuddy.transfer

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

enum class TransferMode {
    Send,
    Receive
}

data class BluetoothPeer(
    val name: String,
    val address: String
)

data class BluetoothTransferUiState(
    val supported: Boolean = true,
    val inProgress: Boolean = false,
    val statusMessage: String = "",
    val peers: List<BluetoothPeer> = emptyList(),
    val completed: Boolean = false,
    val details: List<String> = emptyList()
)

interface BluetoothTransferController {
    val uiState: StateFlow<BluetoothTransferUiState>
    fun start()
    fun retry()
    fun selectPeer(address: String)
    fun stop()
}

@Composable
expect fun rememberBluetoothTransferController(mode: TransferMode): BluetoothTransferController
