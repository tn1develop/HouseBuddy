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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.carpe_diem
import housebuddy.shared.generated.resources.menu_select_option
import housebuddy.shared.generated.resources.nav_menu
import housebuddy.shared.generated.resources.transfer
import org.jetbrains.compose.resources.stringResource

@Composable
fun MenuScreen(
    onTransferClick: () -> Unit,
    onCarpeDiemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.nav_menu),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(Res.string.menu_select_option),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onTransferClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.transfer))
            }

            Button(
                onClick = onCarpeDiemClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.carpe_diem))
            }
        }
    }
}
