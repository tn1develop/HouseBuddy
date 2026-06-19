package com.example.housebuddy.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.housebuddy.presentation.mvi.HousePriceEvent
import com.example.housebuddy.transfer.TransferMode
import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.transfer_receive_subtitle
import housebuddy.shared.generated.resources.transfer_receive_title
import housebuddy.shared.generated.resources.transfer_waiting_screen_send
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReceiveTransferScreen(
    onChangeDirection: () -> Unit,
    onIntent: (HousePriceEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BluetoothConnectionScreen(
        title = stringResource(Res.string.transfer_receive_title),
        subtitle = stringResource(Res.string.transfer_receive_subtitle),
        waitingForScreen = stringResource(Res.string.transfer_waiting_screen_send),
        mode = TransferMode.Receive,
        onChangeDirection = onChangeDirection,
        onTransferCompleted = { onIntent(HousePriceEvent.ReloadFromStorage) },
        modifier = modifier
    )
}
