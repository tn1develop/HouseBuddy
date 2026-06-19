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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.transfer
import housebuddy.shared.generated.resources.transfer_receive_on_phone
import housebuddy.shared.generated.resources.transfer_select_direction
import housebuddy.shared.generated.resources.transfer_send_from_phone
import org.jetbrains.compose.resources.stringResource

@Composable
fun TransferScreen(
    onReceiveClick: () -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.transfer),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.transfer_select_direction),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onReceiveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.transfer_receive_on_phone))
            }

            Button(
                onClick = onSendClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.transfer_send_from_phone))
            }
        }
    }
}
