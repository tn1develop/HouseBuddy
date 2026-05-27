package com.example.housebuddy.transfer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Base64
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.housebuddy.data.local.getHousePriceSharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

private val SERVICE_UUID: UUID = UUID.fromString("3bdca15d-f6cc-4d95-a4fc-a6f0cf54c3a1")
private val CHARACTERISTIC_UUID: UUID = UUID.fromString("a6a4b4d6-0d52-47f8-9d76-1c8e8a04e6f2")
private const val CHUNK_SIZE = 20
private const val TYPE_STRING = "string"
private const val TYPE_BOOLEAN = "bool"
private const val TYPE_INT = "int"
private const val TYPE_LONG = "long"
private const val TYPE_FLOAT = "float"
private const val TYPE_STRING_SET = "set"

@Composable
actual fun rememberBluetoothTransferController(mode: TransferMode): BluetoothTransferController {
    val context = LocalContext.current.applicationContext
    return remember(mode) { AndroidBluetoothTransferController(context, mode) }
}

private class AndroidBluetoothTransferController(
    private val context: Context,
    private val mode: TransferMode
) : BluetoothTransferController {

    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val leScanner: BluetoothLeScanner? get() = adapter?.bluetoothLeScanner
    private val advertiser: BluetoothLeAdvertiser? get() = adapter?.bluetoothLeAdvertiser

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val mutableUiState = MutableStateFlow(
        BluetoothTransferUiState(
            supported = adapter != null,
            statusMessage = if (adapter == null) {
                "Bluetooth non supportato su questo dispositivo."
            } else {
                "Pronto"
            }
        )
    )

    override val uiState: StateFlow<BluetoothTransferUiState> = mutableUiState
    private var runningJob: Job? = null

    private var activeGattServer: BluetoothGattServer? = null
    private var activeGatt: BluetoothGatt? = null
    private var advertised = false

    private val discoveredDevices = linkedMapOf<String, BluetoothDevice>()
    private val inboundBuffer = mutableListOf<Byte>()

    private var targetCharacteristic: BluetoothGattCharacteristic? = null
    private var outboundChunks: List<ByteArray> = emptyList()
    private var outboundIndex = 0
    private var outboundKeys: List<String> = emptyList()
    private var pendingConnectAddress: String? = null
    private var connectRetryCount = 0
    private var pendingAdvertiseSettings: AdvertiseSettings? = null
    private var pendingAdvertiseData: AdvertiseData? = null

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            val device = result?.device ?: return
            val serviceUuids = result.scanRecord?.serviceUuids.orEmpty()
            val hasHouseBuddyService = serviceUuids.any { it.uuid == SERVICE_UUID }
            if (!hasHouseBuddyService) return
            val address = device.address ?: return
            discoveredDevices[address] = device
            publishPeers()
        }

        override fun onScanFailed(errorCode: Int) {
            mutableUiState.update {
                it.copy(inProgress = false, statusMessage = "Errore scansione BLE (codice $errorCode).")
            }
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            advertised = false
            mutableUiState.update {
                it.copy(inProgress = false, statusMessage = "Errore ascolto BLE (codice $errorCode).")
            }
        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
            if (service?.uuid != SERVICE_UUID) return
            if (status != BluetoothGatt.GATT_SUCCESS) {
                mutableUiState.update {
                    it.copy(inProgress = false, statusMessage = "Impossibile registrare servizio BLE (status $status).")
                }
                return
            }
            val settings = pendingAdvertiseSettings ?: return
            val data = pendingAdvertiseData ?: return
            advertiser?.startAdvertising(settings, data, advertiseCallback)
            advertised = true
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            val bytes = value ?: ByteArray(0)
            if (offset == 0) {
                inboundBuffer.addAll(bytes.toList())
            } else {
                while (inboundBuffer.size < offset) inboundBuffer += 0
                bytes.forEachIndexed { index, byte ->
                    val pos = offset + index
                    if (pos < inboundBuffer.size) inboundBuffer[pos] = byte else inboundBuffer += byte
                }
            }
            if (responseNeeded) {
                activeGattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null)
            }
            tryConsumeInboundFrames()
        }

        override fun onExecuteWrite(device: BluetoothDevice?, requestId: Int, execute: Boolean) {
            val status = if (execute) BluetoothGatt.GATT_SUCCESS else BluetoothGatt.GATT_SUCCESS
            activeGattServer?.sendResponse(device, requestId, status, 0, null)
            if (!execute) {
                inboundBuffer.clear()
            } else {
                tryConsumeInboundFrames()
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                val address = pendingConnectAddress
                failSend("Connessione BLE fallita (status $status).")
                runCatching { gatt.close() }
                if (status == 22 && connectRetryCount < 1 && address != null) {
                    connectRetryCount += 1
                    retryConnection(address)
                }
                return
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectRetryCount = 0
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mutableUiState.value.inProgress && outboundIndex < outboundChunks.size) {
                    failSend("Dispositivo disconnesso durante l'invio.")
                }
                runCatching { gatt.close() }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                failSend("Discovery servizi BLE fallita.")
                return
            }
            val characteristic = gatt.getService(SERVICE_UUID)?.getCharacteristic(CHARACTERISTIC_UUID)
            if (characteristic == null) {
                failSend("Servizio HouseBuddy BLE non trovato.")
                return
            }
            targetCharacteristic = characteristic
            outboundIndex = 0
            writeNextChunk(gatt)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                failSend("Invio BLE fallito (status $status).")
                runCatching { gatt.disconnect() }
                return
            }
            outboundIndex += 1
            if (outboundIndex >= outboundChunks.size) {
                mutableUiState.update {
                    it.copy(
                        inProgress = false,
                        completed = true,
                        statusMessage = "Settings inviati con successo.",
                        details = buildTransferDetails(outboundKeys)
                    )
                }
                runCatching { gatt.disconnect() }
            } else {
                writeNextChunk(gatt)
            }
        }
    }

    override fun start() {
        if (adapter == null) {
            mutableUiState.update { it.copy(supported = false, statusMessage = "Bluetooth non disponibile.") }
            return
        }
        if (!hasConnectPermission()) {
            mutableUiState.update {
                it.copy(
                    inProgress = false,
                    statusMessage = "Permesso Bluetooth mancante. Consenti 'Dispositivi nelle vicinanze' nelle impostazioni app."
                )
            }
            return
        }
        if (!adapter.isEnabled) {
            mutableUiState.update {
                it.copy(inProgress = false, statusMessage = "Bluetooth disattivato. Attivalo e riprova.")
            }
            return
        }
        stopCurrentOperation()
        if (mode == TransferMode.Receive) startListening() else startScanning()
    }

    override fun retry() = start()

    @SuppressLint("MissingPermission")
    override fun selectPeer(address: String) {
        if (mode != TransferMode.Send) return
        if (!hasConnectPermission()) {
            mutableUiState.update { it.copy(statusMessage = "Permesso Bluetooth mancante. Impossibile connettere.") }
            return
        }
        val device = discoveredDevices[address]
        if (device == null) {
            mutableUiState.update { it.copy(statusMessage = "Dispositivo non trovato. Aggiorna la lista e riprova.") }
            return
        }
        runCatching { leScanner?.stopScan(scanCallback) }
        val payload = exportSettingsPayload()
        outboundKeys = payload.keys
        outboundChunks = framePayload(payload.raw, CHUNK_SIZE)
        outboundIndex = 0
        pendingConnectAddress = address
        connectRetryCount = 0

        mutableUiState.update {
            it.copy(
                inProgress = true,
                completed = false,
                details = emptyList(),
                statusMessage = "Connessione al dispositivo in corso..."
            )
        }
        activeGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, gattCallback)
        }
    }

    override fun stop() {
        stopCurrentOperation()
        scope.cancel()
    }

    @SuppressLint("MissingPermission")
    private fun stopCurrentOperation() {
        runningJob?.cancel()
        runningJob = null
        runCatching { leScanner?.stopScan(scanCallback) }
        if (advertised) runCatching { advertiser?.stopAdvertising(advertiseCallback) }
        runCatching { activeGattServer?.close() }
        runCatching { activeGatt?.disconnect() }
        runCatching { activeGatt?.close() }
        activeGattServer = null
        activeGatt = null
        advertised = false
        discoveredDevices.clear()
        inboundBuffer.clear()
        targetCharacteristic = null
        outboundChunks = emptyList()
        outboundKeys = emptyList()
        outboundIndex = 0
        pendingConnectAddress = null
        connectRetryCount = 0
        pendingAdvertiseSettings = null
        pendingAdvertiseData = null
    }

    @SuppressLint("MissingPermission")
    private fun startListening() {
        if (!hasAdvertisePermission()) {
            mutableUiState.update { it.copy(inProgress = false, statusMessage = "Permesso advertising Bluetooth mancante.") }
            return
        }
        mutableUiState.update {
            it.copy(
                peers = emptyList(),
                inProgress = true,
                completed = false,
                details = emptyList(),
                statusMessage = "In ascolto di un dispositivo in invio..."
            )
        }
        val manager = bluetoothManager ?: run {
            mutableUiState.update { it.copy(inProgress = false, statusMessage = "Bluetooth manager non disponibile.") }
            return
        }
        activeGattServer = manager.openGattServer(context, gattServerCallback)
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val characteristic = BluetoothGattCharacteristic(
            CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(characteristic)
        val serviceAdded = activeGattServer?.addService(service) == true
        if (!serviceAdded) {
            mutableUiState.update { it.copy(inProgress = false, statusMessage = "Impossibile creare servizio BLE.") }
            return
        }
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(true)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()
        val data = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(SERVICE_UUID))
            .setIncludeDeviceName(false)
            .build()
        pendingAdvertiseSettings = settings
        pendingAdvertiseData = data
    }

    @SuppressLint("MissingPermission")
    private fun startScanning() {
        if (!hasScanPermission()) {
            mutableUiState.update { it.copy(inProgress = false, statusMessage = "Permesso scansione Bluetooth mancante.") }
            return
        }
        discoveredDevices.clear()
        mutableUiState.update {
            it.copy(
                peers = emptyList(),
                inProgress = true,
                completed = false,
                details = emptyList(),
                statusMessage = "Ricerca dispositivi BLE in corso..."
            )
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        runCatching { leScanner?.startScan(null, settings, scanCallback) }
            .onFailure { error ->
                mutableUiState.update { it.copy(inProgress = false, statusMessage = "Errore scansione BLE: ${error.message}") }
            }
    }

    @SuppressLint("MissingPermission")
    private fun writeNextChunk(gatt: BluetoothGatt) {
        val characteristic = targetCharacteristic ?: run {
            failSend("Caratteristica BLE non disponibile.")
            return
        }
        if (outboundIndex >= outboundChunks.size) return
        characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        characteristic.value = outboundChunks[outboundIndex]
        if (!gatt.writeCharacteristic(characteristic)) {
            failSend("Impossibile inviare chunk BLE.")
        }
    }

    private fun tryConsumeInboundFrames() {
        while (true) {
            val payload = tryReadFramedPayload(inboundBuffer) ?: return
            runningJob?.cancel()
            runningJob = scope.launch {
                val result = withContext(Dispatchers.IO) { runCatching { importSettingsPayload(payload) } }
                result.fold(
                    onSuccess = { keys ->
                        mutableUiState.update {
                            it.copy(
                                inProgress = false,
                                completed = true,
                                statusMessage = "Settings ricevuti e salvati con successo.",
                                details = buildTransferDetails(keys)
                            )
                        }
                    },
                    onFailure = { error ->
                        mutableUiState.update {
                            it.copy(
                                inProgress = false,
                                completed = false,
                                statusMessage = "Ricezione fallita: ${error.message ?: "errore sconosciuto"}",
                                details = emptyList()
                            )
                        }
                    }
                )
            }
        }
    }

    private fun hasConnectPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasScanPermission(): Boolean {
        val hasLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return hasLocation
        }
        val hasBleScan = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
        return hasBleScan && hasLocation
    }

    private fun hasAdvertisePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
    }

    private fun publishPeers() {
        val peers = discoveredDevices.values
            .map { BluetoothPeer(name = it.name ?: "Dispositivo senza nome", address = it.address) }
            .sortedBy { it.name.lowercase() }
        mutableUiState.update {
            it.copy(
                peers = peers,
                inProgress = false,
                statusMessage = if (peers.isEmpty()) {
                    "Nessun dispositivo trovato. Verifica Bluetooth e riprova."
                } else {
                    "Seleziona il telefono in modalità ricezione."
                }
            )
        }
    }

    private fun failSend(message: String) {
        mutableUiState.update { it.copy(inProgress = false, completed = false, statusMessage = message) }
    }

    @SuppressLint("MissingPermission")
    private fun retryConnection(address: String) {
        val device = discoveredDevices[address] ?: adapter?.getRemoteDevice(address)
        if (device == null) return
        activeGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            device.connectGatt(context, false, gattCallback)
        }
    }
}

private fun framePayload(payload: String, chunkSize: Int): List<ByteArray> {
    val payloadBytes = payload.toByteArray(Charsets.UTF_8)
    val framed = ByteArray(4 + payloadBytes.size)
    val length = payloadBytes.size
    framed[0] = ((length ushr 24) and 0xFF).toByte()
    framed[1] = ((length ushr 16) and 0xFF).toByte()
    framed[2] = ((length ushr 8) and 0xFF).toByte()
    framed[3] = (length and 0xFF).toByte()
    payloadBytes.copyInto(framed, destinationOffset = 4)
    val chunks = mutableListOf<ByteArray>()
    var offset = 0
    while (offset < framed.size) {
        val size = minOf(chunkSize, framed.size - offset)
        chunks += framed.copyOfRange(offset, offset + size)
        offset += size
    }
    return chunks
}

private fun tryReadFramedPayload(buffer: MutableList<Byte>): String? {
    if (buffer.size < 4) return null
    val length = ((buffer[0].toInt() and 0xFF) shl 24) or
        ((buffer[1].toInt() and 0xFF) shl 16) or
        ((buffer[2].toInt() and 0xFF) shl 8) or
        (buffer[3].toInt() and 0xFF)
    if (length < 0 || buffer.size < 4 + length) return null
    repeat(4) { buffer.removeAt(0) }
    val payloadBytes = ByteArray(length) { index -> buffer.removeAt(0) }
    return String(payloadBytes, Charsets.UTF_8)
}

private data class SettingsPayload(val raw: String, val keys: List<String>)

private fun exportSettingsPayload(): SettingsPayload {
    val prefs = getHousePriceSharedPreferences()
    val keys = mutableListOf<String>()
    val raw = prefs.all.entries.joinToString(separator = "\n") { entry ->
        val key = entry.key
        keys += key
        val (type, value) = when (val rawValue = entry.value) {
            is String -> TYPE_STRING to rawValue
            is Boolean -> TYPE_BOOLEAN to rawValue.toString()
            is Int -> TYPE_INT to rawValue.toString()
            is Long -> TYPE_LONG to rawValue.toString()
            is Float -> TYPE_FLOAT to rawValue.toString()
            is Set<*> -> TYPE_STRING_SET to rawValue.joinToString("\u0001") { it.toString() }
            else -> TYPE_STRING to rawValue.toString()
        }
        val encoded = Base64.encodeToString(value.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        "$key|$type|$encoded"
    }
    return SettingsPayload(raw = raw, keys = keys.sorted())
}

private fun importSettingsPayload(payload: String): List<String> {
    val prefs = getHousePriceSharedPreferences()
    val editor = prefs.edit()
    val importedKeys = mutableListOf<String>()
    payload.lineSequence()
        .filter { it.isNotBlank() }
        .forEach { line ->
            val parts = line.split("|", limit = 3)
            if (parts.size != 3) return@forEach
            val key = parts[0]
            val type = parts[1]
            val rawValue = runCatching {
                String(Base64.decode(parts[2], Base64.NO_WRAP), Charsets.UTF_8)
            }.getOrNull() ?: return@forEach
            importedKeys += key
            when (type) {
                TYPE_STRING -> editor.putString(key, rawValue)
                TYPE_BOOLEAN -> editor.putBoolean(key, rawValue.toBooleanStrictOrNull() ?: false)
                TYPE_INT -> editor.putInt(key, rawValue.toIntOrNull() ?: 0)
                TYPE_LONG -> editor.putLong(key, rawValue.toLongOrNull() ?: 0L)
                TYPE_FLOAT -> editor.putFloat(key, rawValue.toFloatOrNull() ?: 0f)
                TYPE_STRING_SET -> editor.putStringSet(
                    key,
                    if (rawValue.isBlank()) emptySet() else rawValue.split("\u0001").toSet()
                )
            }
        }
    editor.commit()
    return importedKeys.sorted()
}

private fun buildTransferDetails(keys: List<String>): List<String> {
    if (keys.isEmpty()) return listOf("Nessun setting trasferito.")
    val preview = keys.take(5).joinToString(", ")
    val extra = if (keys.size > 5) " (+${keys.size - 5} altri)" else ""
    return listOf(
        "Settings trasferiti: ${keys.size}",
        "Chiavi: $preview$extra"
    )
}
