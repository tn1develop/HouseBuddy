package com.example.housebuddy.transfer

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
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
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID

private const val SERVICE_NAME = "HouseBuddyTransfer"
private val SERVICE_UUID: UUID = UUID.fromString("3bdca15d-f6cc-4d95-a4fc-a6f0cf54c3a1")
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
    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null
    private var discoveryRegistered = false
    private val discoveredDevices = linkedMapOf<String, BluetoothPeer>()
    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val address = device?.address ?: return
                    discoveredDevices[address] = BluetoothPeer(
                        name = device.name ?: "Dispositivo senza nome",
                        address = address
                    )
                    publishPeers()
                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    mutableUiState.update {
                        if (it.inProgress) it else {
                            it.copy(
                                statusMessage = if (it.peers.isEmpty()) {
                                    "Nessun dispositivo trovato. Verifica Bluetooth e riprova."
                                } else {
                                    "Seleziona il telefono in modalità ricezione."
                                }
                            )
                        }
                    }
                }
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
                it.copy(
                    inProgress = false,
                    statusMessage = "Bluetooth disattivato. Attivalo e riprova."
                )
            }
            return
        }

        stopCurrentOperation()
        if (mode == TransferMode.Receive) {
            startListening()
        } else {
            loadPairedDevices()
        }
    }

    override fun retry() {
        start()
    }

    override fun selectPeer(address: String) {
        if (mode != TransferMode.Send) return
        if (adapter == null) return
        if (!hasConnectPermission()) {
            mutableUiState.update {
                it.copy(statusMessage = "Permesso Bluetooth mancante. Impossibile connettere.")
            }
            return
        }

        runningJob?.cancel()
        runningJob = scope.launch {
            mutableUiState.update {
                it.copy(
                    inProgress = true,
                    completed = false,
                    statusMessage = "Connessione al dispositivo in corso..."
                )
            }
            val result = withContext(Dispatchers.IO) {
                runCatching { connectAndSend(address) }
            }
            result.fold(
                onSuccess = {
                    mutableUiState.update {
                        it.copy(
                            inProgress = false,
                            completed = true,
                            statusMessage = "Settings inviati con successo."
                        )
                    }
                },
                onFailure = { error ->
                    mutableUiState.update {
                        it.copy(
                            inProgress = false,
                            completed = false,
                            statusMessage = "Invio fallito: ${error.message ?: "errore sconosciuto"}"
                        )
                    }
                }
            )
        }
    }

    override fun stop() {
        stopCurrentOperation()
        scope.cancel()
    }

    private fun stopCurrentOperation() {
        runningJob?.cancel()
        runningJob = null
        runCatching { adapter?.cancelDiscovery() }
        unregisterDiscoveryReceiver()
        runCatching { serverSocket?.close() }
        runCatching { clientSocket?.close() }
        serverSocket = null
        clientSocket = null
    }

    @SuppressLint("MissingPermission")
    private fun startListening() {
        runningJob = scope.launch {
            mutableUiState.update {
                it.copy(
                    inProgress = true,
                    completed = false,
                    statusMessage = "In ascolto di un dispositivo in invio..."
                )
            }
            val result = withContext(Dispatchers.IO) {
                runCatching {
                    serverSocket = adapter?.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID)
                    val acceptedSocket = serverSocket?.accept()
                        ?: error("Impossibile aprire la connessione in ingresso.")
                    clientSocket = acceptedSocket
                    val payload = readPayload(acceptedSocket)
                    importSettingsPayload(payload)
                }
            }

            result.fold(
                onSuccess = { importedKeys ->
                    mutableUiState.update {
                        it.copy(
                            inProgress = false,
                            completed = true,
                            statusMessage = "Settings ricevuti e salvati con successo.",
                            details = buildTransferDetails(importedKeys)
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
            stopCurrentOperation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadPairedDevices() {
        runningJob = scope.launch {
            discoveredDevices.clear()
            val paired = adapter?.bondedDevices.orEmpty()
                .map { device ->
                    BluetoothPeer(
                        name = device.name ?: "Dispositivo senza nome",
                        address = device.address
                    )
                }
                .sortedBy { it.name.lowercase() }

            mutableUiState.update {
                it.copy(
                    peers = paired,
                    inProgress = hasScanPermission(),
                    completed = false,
                    details = emptyList(),
                    statusMessage = if (paired.isEmpty()) {
                        "Ricerca dispositivi in corso. Associa i telefoni se non compaiono."
                    } else {
                        "Ricerca dispositivi in corso. Seleziona il telefono in modalità ricezione."
                    }
                )
            }
            startDiscovery()
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectAndSend(address: String) {
        val device: BluetoothDevice = adapter?.getRemoteDevice(address)
            ?: error("Dispositivo non trovato.")
        adapter.cancelDiscovery()
        clientSocket = device.createRfcommSocketToServiceRecord(SERVICE_UUID)
        clientSocket?.connect()
        val payload = exportSettingsPayload()
        writePayload(clientSocket ?: error("Socket non disponibile."), payload.raw)
        mutableUiState.update {
            it.copy(
                details = buildTransferDetails(payload.keys)
            )
        }
    }

    private fun hasConnectPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasScanPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        if (!hasScanPermission()) {
            mutableUiState.update {
                it.copy(
                    inProgress = false,
                    statusMessage = "Permesso scansione Bluetooth mancante: mostra solo dispositivi associati."
                )
            }
            return
        }
        registerDiscoveryReceiver()
        runCatching {
            adapter?.cancelDiscovery()
            val started = adapter?.startDiscovery() == true
            if (!started) {
                mutableUiState.update {
                    it.copy(
                        inProgress = false,
                        statusMessage = "Impossibile avviare discovery Bluetooth."
                    )
                }
            }
        }.onFailure { error ->
            mutableUiState.update {
                it.copy(
                    inProgress = false,
                    statusMessage = "Errore discovery: ${error.message ?: "sconosciuto"}"
                )
            }
        }
    }

    private fun registerDiscoveryReceiver() {
        if (discoveryRegistered) return
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        discoveryRegistered = true
    }

    private fun unregisterDiscoveryReceiver() {
        if (!discoveryRegistered) return
        runCatching { context.unregisterReceiver(discoveryReceiver) }
        discoveryRegistered = false
    }

    private fun publishPeers() {
        val current = mutableUiState.value.peers.associateBy { it.address }.toMutableMap()
        current.putAll(discoveredDevices)
        val merged = current.values.sortedBy { it.name.lowercase() }
        mutableUiState.update {
            it.copy(
                peers = merged,
                inProgress = false,
                statusMessage = if (merged.isEmpty()) {
                    "Nessun dispositivo trovato. Verifica Bluetooth e riprova."
                } else {
                    "Seleziona il telefono in modalità ricezione."
                }
            )
        }
    }
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

private fun readPayload(socket: BluetoothSocket): String {
    val input = DataInputStream(socket.inputStream)
    val length = input.readInt()
    require(length >= 0) { "Payload non valido." }
    val bytes = ByteArray(length)
    input.readFully(bytes)
    return String(bytes, Charsets.UTF_8)
}

private fun writePayload(socket: BluetoothSocket, payload: String) {
    val output = DataOutputStream(socket.outputStream)
    val bytes = payload.toByteArray(Charsets.UTF_8)
    output.writeInt(bytes.size)
    output.write(bytes)
    output.flush()
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
