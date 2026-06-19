package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.housebuddy.transfer.TransferMode
import com.example.housebuddy.transfer.rememberBluetoothTransferController
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.transfer_change_direction
import housebuddy.shared.generated.resources.transfer_operation_in_progress
import housebuddy.shared.generated.resources.transfer_paired_devices
import housebuddy.shared.generated.resources.transfer_receive_in_progress
import housebuddy.shared.generated.resources.transfer_refresh_bluetooth_devices
import housebuddy.shared.generated.resources.transfer_restart_bluetooth_listen
import housebuddy.shared.generated.resources.transfer_retry_connection
import housebuddy.shared.generated.resources.transfer_send_in_progress
import housebuddy.shared.generated.resources.transfer_waiting_instruction
import org.jetbrains.compose.resources.stringResource

@Composable
fun BluetoothConnectionScreen(
    title: String,
    subtitle: String,
    waitingForScreen: String,
    mode: TransferMode,
    onChangeDirection: () -> Unit,
    onTransferCompleted: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val controller = rememberBluetoothTransferController(mode)
    val uiState by controller.uiState.collectAsState()

    LaunchedEffect(mode) {
        controller.start()
    }
    DisposableEffect(mode) {
        onDispose { controller.stop() }
    }
    LaunchedEffect(uiState.completed, mode) {
        if (mode == TransferMode.Receive && uiState.completed) {
            onTransferCompleted()
        }
    }

    val waitingText = stringResource(Res.string.transfer_waiting_instruction, waitingForScreen)
    val inProgressText = when (mode) {
        TransferMode.Receive -> stringResource(Res.string.transfer_receive_in_progress)
        TransferMode.Send -> stringResource(Res.string.transfer_send_in_progress)
    }
    val retryLabel = if (uiState.inProgress) {
        stringResource(Res.string.transfer_operation_in_progress)
    } else {
        stringResource(Res.string.transfer_retry_connection)
    }
    val refreshLabel = if (mode == TransferMode.Receive) {
        stringResource(Res.string.transfer_restart_bluetooth_listen)
    } else {
        stringResource(Res.string.transfer_refresh_bluetooth_devices)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = uiState.statusMessage,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = if (uiState.inProgress) inProgressText else waitingText,
            style = MaterialTheme.typography.bodyMedium
        )
        if (uiState.details.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            uiState.details.forEach { detail ->
                Text(
                    text = "• $detail",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (mode == TransferMode.Send && uiState.peers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.transfer_paired_devices),
                style = MaterialTheme.typography.titleSmall
            )
            uiState.peers.forEach { peer ->
                OutlinedButton(
                    onClick = { controller.selectPeer(peer.address) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.inProgress
                ) {
                    Text("${peer.name} (${peer.address})")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { controller.retry() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.inProgress
        ) {
            Text(retryLabel)
        }

        OutlinedButton(
            onClick = { controller.start() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(refreshLabel)
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onChangeDirection,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(Res.string.transfer_change_direction))
        }
    }
}
