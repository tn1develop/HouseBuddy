package com.example.housebuddy.resources

import housebuddy.shared.generated.resources.Res
import housebuddy.shared.generated.resources.transfer_ble_characteristic_unavailable
import housebuddy.shared.generated.resources.transfer_ble_chunk_send_failed
import housebuddy.shared.generated.resources.transfer_ble_connection_failed
import housebuddy.shared.generated.resources.transfer_ble_disconnected_during_send
import housebuddy.shared.generated.resources.transfer_ble_listen_error
import housebuddy.shared.generated.resources.transfer_ble_scan_error
import housebuddy.shared.generated.resources.transfer_ble_scan_failed
import housebuddy.shared.generated.resources.transfer_ble_send_failed
import housebuddy.shared.generated.resources.transfer_ble_service_create_failed
import housebuddy.shared.generated.resources.transfer_ble_service_discovery_failed
import housebuddy.shared.generated.resources.transfer_ble_service_error
import housebuddy.shared.generated.resources.transfer_ble_service_not_found
import housebuddy.shared.generated.resources.transfer_ble_service_register_error
import housebuddy.shared.generated.resources.transfer_bluetooth_connect_permission_missing
import housebuddy.shared.generated.resources.transfer_bluetooth_disabled
import housebuddy.shared.generated.resources.transfer_bluetooth_manager_unavailable
import housebuddy.shared.generated.resources.transfer_bluetooth_not_supported
import housebuddy.shared.generated.resources.transfer_bluetooth_permission_missing
import housebuddy.shared.generated.resources.transfer_bluetooth_unavailable
import housebuddy.shared.generated.resources.transfer_advertise_permission_missing
import housebuddy.shared.generated.resources.transfer_connecting
import housebuddy.shared.generated.resources.transfer_device_not_found
import housebuddy.shared.generated.resources.transfer_device_unnamed
import housebuddy.shared.generated.resources.transfer_extra_others
import housebuddy.shared.generated.resources.transfer_keys_preview
import housebuddy.shared.generated.resources.transfer_listening_for_sender
import housebuddy.shared.generated.resources.transfer_no_devices_found
import housebuddy.shared.generated.resources.transfer_no_settings
import housebuddy.shared.generated.resources.transfer_ready
import housebuddy.shared.generated.resources.transfer_receive_failed
import housebuddy.shared.generated.resources.transfer_receive_success
import housebuddy.shared.generated.resources.transfer_scan_permission_missing
import housebuddy.shared.generated.resources.transfer_scanning_devices
import housebuddy.shared.generated.resources.transfer_select_receiving_phone
import housebuddy.shared.generated.resources.transfer_send_failed
import housebuddy.shared.generated.resources.transfer_send_success
import housebuddy.shared.generated.resources.transfer_settings_count
import housebuddy.shared.generated.resources.transfer_unknown_error
import org.jetbrains.compose.resources.getString

suspend fun transferReady(): String = getString(Res.string.transfer_ready)

suspend fun transferBluetoothNotSupported(): String = getString(Res.string.transfer_bluetooth_not_supported)

suspend fun transferBluetoothUnavailable(): String = getString(Res.string.transfer_bluetooth_unavailable)

suspend fun transferBluetoothPermissionMissing(): String =
    getString(Res.string.transfer_bluetooth_permission_missing)

suspend fun transferBluetoothDisabled(): String = getString(Res.string.transfer_bluetooth_disabled)

suspend fun transferBluetoothConnectPermissionMissing(): String =
    getString(Res.string.transfer_bluetooth_connect_permission_missing)

suspend fun transferDeviceNotFound(): String = getString(Res.string.transfer_device_not_found)

suspend fun transferConnecting(): String = getString(Res.string.transfer_connecting)

suspend fun transferBleScanError(code: Int): String = getString(Res.string.transfer_ble_scan_error, code)

suspend fun transferBleListenError(code: Int): String = getString(Res.string.transfer_ble_listen_error, code)

suspend fun transferBleServiceRegisterError(status: Int): String =
    getString(Res.string.transfer_ble_service_register_error, status)

suspend fun transferBleConnectionFailed(status: Int): String =
    getString(Res.string.transfer_ble_connection_failed, status)

suspend fun transferBleDisconnectedDuringSend(): String =
    getString(Res.string.transfer_ble_disconnected_during_send)

suspend fun transferBleServiceDiscoveryFailed(): String =
    getString(Res.string.transfer_ble_service_discovery_failed)

suspend fun transferBleServiceNotFound(): String = getString(Res.string.transfer_ble_service_not_found)

suspend fun transferBleSendFailed(status: Int): String = getString(Res.string.transfer_ble_send_failed, status)

suspend fun transferSendSuccess(): String = getString(Res.string.transfer_send_success)

suspend fun transferAdvertisePermissionMissing(): String =
    getString(Res.string.transfer_advertise_permission_missing)

suspend fun transferListeningForSender(): String = getString(Res.string.transfer_listening_for_sender)

suspend fun transferBluetoothManagerUnavailable(): String =
    getString(Res.string.transfer_bluetooth_manager_unavailable)

suspend fun transferBleServiceCreateFailed(): String = getString(Res.string.transfer_ble_service_create_failed)

suspend fun transferScanPermissionMissing(): String = getString(Res.string.transfer_scan_permission_missing)

suspend fun transferScanningDevices(): String = getString(Res.string.transfer_scanning_devices)

suspend fun transferBleScanFailed(message: String): String =
    getString(Res.string.transfer_ble_scan_failed, message)

suspend fun transferBleCharacteristicUnavailable(): String =
    getString(Res.string.transfer_ble_characteristic_unavailable)

suspend fun transferBleChunkSendFailed(): String = getString(Res.string.transfer_ble_chunk_send_failed)

suspend fun transferReceiveSuccess(): String = getString(Res.string.transfer_receive_success)

suspend fun transferReceiveFailed(message: String): String =
    getString(Res.string.transfer_receive_failed, message)

suspend fun transferUnknownError(): String = getString(Res.string.transfer_unknown_error)

suspend fun transferDeviceUnnamed(): String = getString(Res.string.transfer_device_unnamed)

suspend fun transferNoDevicesFound(): String = getString(Res.string.transfer_no_devices_found)

suspend fun transferSelectReceivingPhone(): String = getString(Res.string.transfer_select_receiving_phone)

suspend fun transferBleServiceError(message: String): String =
    getString(Res.string.transfer_ble_service_error, message)

suspend fun transferSendFailed(message: String): String = getString(Res.string.transfer_send_failed, message)

suspend fun buildTransferDetails(keys: List<String>): List<String> {
    if (keys.isEmpty()) return listOf(getString(Res.string.transfer_no_settings))
    val preview = keys.take(5).joinToString(", ")
    val extra = if (keys.size > 5) {
        getString(Res.string.transfer_extra_others, keys.size - 5)
    } else {
        ""
    }
    return listOf(
        getString(Res.string.transfer_settings_count, keys.size),
        getString(Res.string.transfer_keys_preview, preview, extra)
    )
}
