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

@Composable
fun BluetoothConnectionScreen(
    title: String,
    subtitle: String,
    waitingForScreen: String,
    mode: TransferMode,
    onChangeDirection: () -> Unit,
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

    val waitingText = "Tieni aperta anche la schermata \"$waitingForScreen\" sull'altro telefono."
    val inProgressText = when (mode) {
        TransferMode.Receive -> "In ascolto per ricevere i settings via Bluetooth."
        TransferMode.Send -> "Seleziona un dispositivo e avvia l'invio dei settings."
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
                text = "Dispositivi associati",
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
            Text(if (uiState.inProgress) "Operazione in corso..." else "Riprova connessione")
        }

        OutlinedButton(
            onClick = { controller.start() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (mode == TransferMode.Receive) "Riavvia ascolto Bluetooth" else "Aggiorna dispositivi Bluetooth")
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(
            onClick = onChangeDirection,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cambia direzione")
        }
    }
}
