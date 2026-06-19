package com.example.housebuddy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.housebuddy.domain.util.formatEuroAmount
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.ic_info
import housebuddy.shared.generated.resources.info
import housebuddy.shared.generated.resources.ok
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultField(label: String, value: Double, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = formatEuroAmount(value),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ResultFieldCompact(label: String, value: Double, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = formatEuroAmount(value),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InfoIcon(
    infoText: String,
    dialogTitle: String,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(dialogTitle) },
            text = { Text(infoText) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(Res.string.ok))
                }
            }
        )
    }

    Icon(
        painter = painterResource(Res.drawable.ic_info),
        contentDescription = stringResource(Res.string.info),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .size(18.dp)
            .clickable { showDialog = true }
    )
}
