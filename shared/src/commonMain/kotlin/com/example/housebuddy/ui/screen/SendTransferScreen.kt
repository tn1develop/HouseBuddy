package com.example.housebuddy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.housebuddy.transfer.TransferMode
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.transfer_send_subtitle
import housebuddy.shared.generated.resources.transfer_send_title
import housebuddy.shared.generated.resources.transfer_waiting_screen_receive
import org.jetbrains.compose.resources.stringResource

@Composable
fun SendTransferScreen(
    onChangeDirection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BluetoothConnectionScreen(
        title = stringResource(Res.string.transfer_send_title),
        subtitle = stringResource(Res.string.transfer_send_subtitle),
        waitingForScreen = stringResource(Res.string.transfer_waiting_screen_receive),
        mode = TransferMode.Send,
        onChangeDirection = onChangeDirection,
        modifier = modifier
    )
}
