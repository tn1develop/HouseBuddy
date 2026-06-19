package com.example.housebuddy.transfer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import com.example.housebuddy.resources.buildTransferDetails
import com.example.housebuddy.resources.transferBleServiceError
import com.example.housebuddy.resources.transferConnecting
import com.example.housebuddy.resources.transferDeviceNotFound
import com.example.housebuddy.resources.transferDeviceUnnamed
import com.example.housebuddy.resources.transferListeningForSender
import com.example.housebuddy.resources.transferNoDevicesFound
import com.example.housebuddy.resources.transferReady
import com.example.housebuddy.resources.transferReceiveFailed
import com.example.housebuddy.resources.transferReceiveSuccess
import com.example.housebuddy.resources.transferScanningDevices
import com.example.housebuddy.resources.transferSelectReceivingPhone
import com.example.housebuddy.resources.transferSendFailed
import com.example.housebuddy.resources.transferSendSuccess
import com.example.housebuddy.resources.transferUnknownError
import platform.CoreBluetooth.CBATTErrorSuccess
import platform.CoreBluetooth.CBATTRequest
import platform.CoreBluetooth.CBAdvertisementDataServiceUUIDsKey
import platform.CoreBluetooth.CBAttributePermissionsWriteable
import platform.CoreBluetooth.CBCentralManager
import platform.CoreBluetooth.CBCentralManagerDelegateProtocol
import platform.CoreBluetooth.CBCharacteristic
import platform.CoreBluetooth.CBCharacteristicPropertyWrite
import platform.CoreBluetooth.CBCharacteristicWriteWithResponse
import platform.CoreBluetooth.CBManagerStatePoweredOn
import platform.CoreBluetooth.CBMutableCharacteristic
import platform.CoreBluetooth.CBMutableService
import platform.CoreBluetooth.CBPeripheral
import platform.CoreBluetooth.CBPeripheralDelegateProtocol
import platform.CoreBluetooth.CBPeripheralManager
import platform.CoreBluetooth.CBPeripheralManagerDelegateProtocol
import platform.CoreBluetooth.CBService
import platform.CoreBluetooth.CBUUID
import platform.Foundation.NSArray
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy

@Composable
actual fun rememberBluetoothTransferController(mode: TransferMode): BluetoothTransferController {
    return remember(mode) { IosBluetoothTransferController(mode) }
}

private const val BLE_SERVICE_UUID = "3BDCA15D-F6CC-4D95-A4FC-A6F0CF54C3A1"
private const val BLE_CHARACTERISTIC_UUID = "A6A4B4D6-0D52-47F8-9D76-1C8E8A04E6F2"
private const val BLE_CHUNK_SIZE = 20
private const val TYPE_STRING = "string"
private const val TYPE_BOOLEAN = "bool"
private const val TYPE_INT = "int"
private const val TYPE_LONG = "long"
private const val TYPE_FLOAT = "float"
private const val TYPE_STRING_SET = "set"

private class IosBluetoothTransferController(
    private val mode: TransferMode
) : BluetoothTransferController {

    private val mutableUiState = MutableStateFlow(
        BluetoothTransferUiState(supported = true, inProgress = false)
    )
    override val uiState: StateFlow<BluetoothTransferUiState> = mutableUiState

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    init {
        scope.launch {
            mutableUiState.update { it.copy(statusMessage = transferReady()) }
        }
    }

    private var runningJob: Job? = null

    private val serviceUuid = CBUUID.UUIDWithString(BLE_SERVICE_UUID)
    private val characteristicUuid = CBUUID.UUIDWithString(BLE_CHARACTERISTIC_UUID)

    private var peripheralManager: CBPeripheralManager? = null
    private var centralManager: CBCentralManager? = null
    private val discovered = linkedMapOf<String, CBPeripheral>()
    private val inboundBuffer = mutableListOf<Byte>()

    private var selectedPeripheralId: String? = null
    private var connectedPeripheral: CBPeripheral? = null
    private var targetCharacteristic: CBCharacteristic? = null
    private var outboundChunks: List<NSData> = emptyList()
    private var outboundIndex = 0
    private var outboundKeys: List<String> = emptyList()

    private val peripheralDelegate = PeripheralDelegate(this)
    private val centralDelegate = CentralDelegate(this)
    private val remotePeripheralDelegate = RemotePeripheralDelegate(this)

    override fun start() {
        stopCurrentOperation()
        if (mode == TransferMode.Receive) startListening() else startBrowsing()
    }

    override fun retry() = start()

    override fun selectPeer(address: String) {
        if (mode != TransferMode.Send) return
        val peripheral = discovered[address] ?: run {
            scope.launch {
                mutableUiState.update { it.copy(statusMessage = transferDeviceNotFound()) }
            }
            return
        }
        selectedPeripheralId = address
        scope.launch {
            mutableUiState.update {
                it.copy(inProgress = true, completed = false, statusMessage = transferConnecting())
            }
        }
        centralManager?.connectPeripheral(peripheral, options = null)
    }

    override fun stop() {
        stopCurrentOperation()
        scope.cancel()
    }

    private fun stopCurrentOperation() {
        runningJob?.cancel()
        runningJob = null
        centralManager?.stopScan()
        connectedPeripheral?.let { centralManager?.cancelPeripheralConnection(it) }
        peripheralManager?.stopAdvertising()
        discovered.clear()
        inboundBuffer.clear()
        selectedPeripheralId = null
        connectedPeripheral = null
        targetCharacteristic = null
        outboundChunks = emptyList()
        outboundIndex = 0
        outboundKeys = emptyList()
        centralManager = null
        peripheralManager = null
    }

    private fun startListening() {
        scope.launch {
            mutableUiState.update {
                it.copy(
                    peers = emptyList(),
                    inProgress = true,
                    completed = false,
                    details = emptyList(),
                    statusMessage = transferListeningForSender()
                )
            }
        }
        peripheralManager = CBPeripheralManager(peripheralDelegate, dispatch_get_main_queue())
    }

    private fun startBrowsing() {
        scope.launch {
            mutableUiState.update {
                it.copy(
                    peers = emptyList(),
                    inProgress = true,
                    completed = false,
                    details = emptyList(),
                    statusMessage = transferScanningDevices()
                )
            }
        }
        centralManager = CBCentralManager(centralDelegate, dispatch_get_main_queue())
    }

    internal fun onPeripheralStatePoweredOn(manager: CBPeripheralManager) {
        val characteristic = CBMutableCharacteristic(
            type = characteristicUuid,
            properties = CBCharacteristicPropertyWrite.toULong(),
            value = null,
            permissions = CBAttributePermissionsWriteable.toULong()
        )
        val service = CBMutableService(type = serviceUuid, primary = true)
        service.setCharacteristics(listOf(characteristic))
        manager.addService(service)
    }

    internal fun onPeripheralServiceAdded(manager: CBPeripheralManager, error: NSError?) {
        if (error != null) {
            scope.launch {
                mutableUiState.update {
                    it.copy(
                        inProgress = false,
                        statusMessage = transferBleServiceError(error.localizedDescription)
                    )
                }
            }
            return
        }
        manager.startAdvertising(
            mapOf(CBAdvertisementDataServiceUUIDsKey to listOf(serviceUuid))
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    internal fun onWriteRequests(manager: CBPeripheralManager, requests: List<*>) {
        requests.filterIsInstance<CBATTRequest>().forEach { request ->
            inboundBuffer.addAll((request.value?.toByteArray() ?: ByteArray(0)).toList())
            manager.respondToRequest(request, CBATTErrorSuccess)
        }
        while (true) {
            val payload = tryReadFramedPayload(inboundBuffer) ?: break
            runningJob?.cancel()
            runningJob = scope.launch {
                runCatching { importSettingsPayload(payload) }
                    .onSuccess { keys ->
                        mutableUiState.update {
                            it.copy(
                                inProgress = false,
                                completed = true,
                                statusMessage = transferReceiveSuccess(),
                                details = buildTransferDetails(keys)
                            )
                        }
                    }
                    .onFailure { error ->
                        mutableUiState.update {
                            it.copy(
                                inProgress = false,
                                completed = false,
                                statusMessage = transferReceiveFailed(
                                    error.message ?: transferUnknownError()
                                ),
                                details = emptyList()
                            )
                        }
                    }
            }
        }
    }

    internal fun onCentralStatePoweredOn(manager: CBCentralManager) {
        manager.scanForPeripheralsWithServices(
            serviceUUIDs = listOf(serviceUuid),
            options = null
        )
    }

    internal fun onDiscoveredPeripheral(peripheral: CBPeripheral) {
        val identifier = peripheral.identifier.UUIDString
        discovered[identifier] = peripheral
        publishPeers()
    }

    internal fun onConnectedPeripheral(peripheral: CBPeripheral) {
        connectedPeripheral = peripheral
        peripheral.delegate = remotePeripheralDelegate
        peripheral.discoverServices(listOf(serviceUuid))
    }

    internal fun onCharacteristicReady(peripheral: CBPeripheral, characteristic: CBCharacteristic) {
        val payload = exportSettingsPayload()
        outboundKeys = payload.keys
        outboundChunks = framePayload(payload.raw, BLE_CHUNK_SIZE)
        outboundIndex = 0
        targetCharacteristic = characteristic
        writeNextChunk(peripheral)
    }

    internal fun onChunkWritten(peripheral: CBPeripheral, error: NSError?) {
        if (error != null) {
            scope.launch {
                mutableUiState.update {
                    it.copy(
                        inProgress = false,
                        completed = false,
                        statusMessage = transferSendFailed(error.localizedDescription)
                    )
                }
            }
            return
        }
        outboundIndex += 1
        if (outboundIndex >= outboundChunks.size) {
            scope.launch {
                mutableUiState.update {
                    it.copy(
                        inProgress = false,
                        completed = true,
                        statusMessage = transferSendSuccess(),
                        details = buildTransferDetails(outboundKeys)
                    )
                }
            }
            return
        }
        writeNextChunk(peripheral)
    }

    private fun writeNextChunk(peripheral: CBPeripheral) {
        val characteristic = targetCharacteristic ?: return
        if (outboundIndex >= outboundChunks.size) return
        peripheral.writeValue(
            data = outboundChunks[outboundIndex],
            forCharacteristic = characteristic,
            type = CBCharacteristicWriteWithResponse
        )
    }

    private fun publishPeers() {
        scope.launch {
            val unnamedDevice = transferDeviceUnnamed()
            val peers = discovered.entries
                .map { (identifier, peripheral) ->
                    BluetoothPeer(
                        name = peripheral.name ?: unnamedDevice,
                        address = identifier
                    )
                }
                .sortedBy { it.name.lowercase() }
            val statusMessage = if (peers.isEmpty()) {
                transferNoDevicesFound()
            } else {
                transferSelectReceivingPhone()
            }
            mutableUiState.update {
                it.copy(
                    peers = peers,
                    inProgress = false,
                    statusMessage = statusMessage
                )
            }
        }
    }
}

private class PeripheralDelegate(
    private val owner: IosBluetoothTransferController
) : NSObject(), CBPeripheralManagerDelegateProtocol {

    override fun peripheralManagerDidUpdateState(peripheral: CBPeripheralManager) {
        if (peripheral.state == CBManagerStatePoweredOn) {
            owner.onPeripheralStatePoweredOn(peripheral)
        }
    }

    override fun peripheralManager(peripheral: CBPeripheralManager, didReceiveWriteRequests: List<*>) {
        owner.onWriteRequests(peripheral, didReceiveWriteRequests)
    }

    override fun peripheralManager(
        peripheral: CBPeripheralManager,
        didAddService: CBService,
        error: NSError?
    ) {
        owner.onPeripheralServiceAdded(peripheral, error)
    }
}

private class CentralDelegate(
    private val owner: IosBluetoothTransferController
) : NSObject(), CBCentralManagerDelegateProtocol {

    override fun centralManagerDidUpdateState(central: CBCentralManager) {
        if (central.state == CBManagerStatePoweredOn) {
            owner.onCentralStatePoweredOn(central)
        }
    }

    override fun centralManager(
        central: CBCentralManager,
        didDiscoverPeripheral: CBPeripheral,
        advertisementData: Map<Any?, *>,
        RSSI: NSNumber
    ) {
        owner.onDiscoveredPeripheral(didDiscoverPeripheral)
    }

    override fun centralManager(central: CBCentralManager, didConnectPeripheral: CBPeripheral) {
        owner.onConnectedPeripheral(didConnectPeripheral)
    }
}

private class RemotePeripheralDelegate(
    private val owner: IosBluetoothTransferController
) : NSObject(), CBPeripheralDelegateProtocol {

    override fun peripheral(peripheral: CBPeripheral, didDiscoverServices: NSError?) {
        if (didDiscoverServices != null) return
        val service = peripheral.services.orEmpty()
            .filterIsInstance<CBService>()
            .firstOrNull { it.UUID.UUIDString == BLE_SERVICE_UUID }
            ?: return
        peripheral.discoverCharacteristics(listOf(CBUUID.UUIDWithString(BLE_CHARACTERISTIC_UUID)), service)
    }

    override fun peripheral(
        peripheral: CBPeripheral,
        didDiscoverCharacteristicsForService: CBService,
        error: NSError?
    ) {
        if (error != null) return
        val characteristic = didDiscoverCharacteristicsForService.characteristics.orEmpty()
            .filterIsInstance<CBCharacteristic>()
            .firstOrNull { it.UUID.UUIDString == BLE_CHARACTERISTIC_UUID }
            ?: return
        owner.onCharacteristicReady(peripheral, characteristic)
    }

    override fun peripheral(
        peripheral: CBPeripheral,
        didWriteValueForCharacteristic: CBCharacteristic,
        error: NSError?
    ) {
        owner.onChunkWritten(peripheral, error)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size <= 0) return ByteArray(0)
    val output = ByteArray(size)
    output.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return output
}

@OptIn(ExperimentalForeignApi::class)
private fun framePayload(payload: String, chunkSize: Int): List<NSData> {
    val payloadBytes = payload.encodeToByteArray()
    val header = byteArrayOf(
        ((payloadBytes.size ushr 24) and 0xFF).toByte(),
        ((payloadBytes.size ushr 16) and 0xFF).toByte(),
        ((payloadBytes.size ushr 8) and 0xFF).toByte(),
        (payloadBytes.size and 0xFF).toByte()
    )
    val framed = header + payloadBytes
    val chunks = mutableListOf<NSData>()
    var offset = 0
    while (offset < framed.size) {
        val size = minOf(chunkSize, framed.size - offset)
        val part = framed.copyOfRange(offset, offset + size)
        part.usePinned { pinned ->
            chunks += NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
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
    val payload = ByteArray(length) { index -> buffer.removeAt(0) }
    return payload.decodeToString()
}

private data class SettingsPayload(val raw: String, val keys: List<String>)

@OptIn(ExperimentalEncodingApi::class)
private fun exportSettingsPayload(): SettingsPayload {
    val prefs = NSUserDefaults.standardUserDefaults
    val entries = prefs.dictionaryRepresentation().entries
    val keys = mutableListOf<String>()
    val raw = entries.joinToString(separator = "\n") { (rawKey, rawValue) ->
        val key = rawKey.toString()
        keys += key
        val (type, value) = encodeSetting(rawValue)
        val encoded = Base64.Default.encode(value.encodeToByteArray())
        "$key|$type|$encoded"
    }
    return SettingsPayload(raw = raw, keys = keys.sorted())
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private fun encodeSetting(rawValue: Any?): Pair<String, String> {
    return when (rawValue) {
        is String -> TYPE_STRING to rawValue
        is NSNumber -> {
            val objcType = rawValue.objCType?.toKString()
            if (objcType == "c" || objcType == "B") {
                TYPE_BOOLEAN to rawValue.boolValue.toString()
            } else {
                val numericString = rawValue.stringValue
                if (numericString.contains('.')) TYPE_FLOAT to numericString else TYPE_LONG to numericString
            }
        }
        is NSArray -> {
            val joined = (0 until rawValue.count.toInt())
                .mapNotNull { index -> rawValue.objectAtIndex(index.toULong())?.toString() }
                .joinToString("\u0001")
            TYPE_STRING_SET to joined
        }
        else -> TYPE_STRING to (rawValue?.toString() ?: "")
    }
}

@OptIn(ExperimentalEncodingApi::class)
private fun importSettingsPayload(payload: String): List<String> {
    val prefs = NSUserDefaults.standardUserDefaults
    val importedKeys = mutableListOf<String>()
    payload.lineSequence()
        .filter { it.isNotBlank() }
        .forEach { line ->
            val parts = line.split("|", limit = 3)
            if (parts.size != 3) return@forEach
            val key = parts[0]
            val type = parts[1]
            val decoded = runCatching { Base64.Default.decode(parts[2]).decodeToString() }.getOrNull() ?: return@forEach
            importedKeys += key
            when (type) {
                TYPE_STRING -> prefs.setObject(decoded, forKey = key)
                TYPE_BOOLEAN -> prefs.setBool(decoded.toBooleanStrictOrNull() ?: false, forKey = key)
                TYPE_INT -> prefs.setInteger(decoded.toLongOrNull() ?: 0L, forKey = key)
                TYPE_LONG -> prefs.setObject(decoded.toLongOrNull() ?: 0L, forKey = key)
                TYPE_FLOAT -> prefs.setFloat(decoded.toDoubleOrNull()?.toFloat() ?: 0f, forKey = key)
                TYPE_STRING_SET -> {
                    val list = if (decoded.isBlank()) emptyList() else decoded.split("\u0001")
                    prefs.setObject(list, forKey = key)
                }
            }
        }
    return importedKeys.sorted()
}

