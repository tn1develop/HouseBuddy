package com.example.housebuddy.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ReceiveTransferScreen(
    onChangeDirection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = "Dati da un altro telefono su questo"
    val subtitle = "In questa modalità questo telefono riceve i dati dall’altro."

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
}