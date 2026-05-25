package com.example.btarduino

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

/**
 * Conexion SPP con el HC-06 / HC-05.
 * UUID estandar de Serial Port Profile.
 */
object BluetoothHelper {

    private val SPP_UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var socket: BluetoothSocket? = null
    private var output: OutputStream? = null

    val isConnected: Boolean
        get() = socket?.isConnected == true

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    fun connect(device: BluetoothDevice) {
        disconnect()
        val s = device.createRfcommSocketToServiceRecord(SPP_UUID)
        s.connect()
        socket = s
        output = s.outputStream
    }

    @Throws(IOException::class)
    fun send(text: String) {
        val out = output ?: throw IOException("No hay conexion Bluetooth")
        out.write(text.toByteArray(Charsets.UTF_8))
        out.flush()
    }

    fun disconnect() {
        try { output?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        output = null
        socket = null
    }
}
