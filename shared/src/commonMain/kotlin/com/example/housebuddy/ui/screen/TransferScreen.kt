package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class TransferDirection {
    ReceiveOnThisPhone,
    SendToOtherPhone,
}

@Composable
fun TransferScreen(
    modifier: Modifier = Modifier
) {

    var selectedDirection: TransferDirection? by remember { mutableStateOf(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Trasferimento",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedDirection == null) {
            Text(
                text = "Seleziona la direzione di trasferimento.",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = { selectedDirection = TransferDirection.ReceiveOnThisPhone },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ricevi su questo telefono")
                }

                Button(
                    onClick = { selectedDirection = TransferDirection.SendToOtherPhone },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Invia da questo telefono")
                }
            }
        } else {
            when (selectedDirection) {
                TransferDirection.ReceiveOnThisPhone -> ReceiveTransferFlow(
                    onChangeDirection = { selectedDirection = null }
                )

                TransferDirection.SendToOtherPhone -> SendTransferFlow(
                    onChangeDirection = { selectedDirection = null }
                )
                else -> "" to ""
            }
        }
    }
}

@Composable
private fun ReceiveTransferFlow(
    onChangeDirection: () -> Unit,
) {
    val title = "Dati da un altro telefono su questo"
    val subtitle = "In questa modalità questo telefono riceve i dati dall’altro."

    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Placeholder: qui andranno i passaggi di trasferimento (es. codice/PAIRING/USB/NFC).
    Text(
        text = "Schermata in arrivo.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    TextButton(
        onClick = onChangeDirection,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cambia direzione")
    }
}

@Composable
private fun SendTransferFlow(
    onChangeDirection: () -> Unit,
) {
    val title = "Dati da questo telefono all’altro"
    val subtitle = "In questa modalità questo telefono invia i dati all’altro."

    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Placeholder: qui andranno i passaggi di trasferimento (es. codice/PAIRING/USB/NFC).
    Text(
        text = "Schermata in arrivo.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(24.dp))

    TextButton(
        onClick = onChangeDirection,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Cambia direzione")
    }
}

