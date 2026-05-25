package com.example.btarduino

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DeviceListActivity : AppCompatActivity() {

    private lateinit var deviceList: RecyclerView
    private lateinit var statusText: TextView
    private lateinit var refreshBtn: Button
    private lateinit var deviceAdapter: DeviceAdapter

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            if (grants.values.all { it }) {
                loadBondedDevices()
            } else {
                statusText.text = getString(R.string.error_permissions)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe llamarse ANTES de super.onCreate(). Aplica Theme.App.Starting
        // como splash y luego cambia a Theme.BluetoothArduino (postSplashScreenTheme).
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        deviceList = findViewById(R.id.deviceList)
        statusText = findViewById(R.id.statusText)
        refreshBtn = findViewById(R.id.refreshBtn)

        deviceAdapter = DeviceAdapter(emptyList()) { device -> connectTo(device) }
        deviceList.layoutManager = LinearLayoutManager(this)
        deviceList.adapter = deviceAdapter

        refreshBtn.setOnClickListener { ensurePermissionsAndLoad() }
    }

    override fun onResume() {
        super.onResume()
        ensurePermissionsAndLoad()
    }

    private fun ensurePermissionsAndLoad() {
        val needed = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                needed.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (needed.isEmpty()) loadBondedDevices()
        else permissionLauncher.launch(needed.toTypedArray())
    }

    @SuppressLint("MissingPermission")
    private fun loadBondedDevices() {
        val mgr = getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = mgr?.adapter
        if (adapter == null) {
            statusText.text = getString(R.string.error_no_bt)
            deviceAdapter.update(emptyList())
            return
        }
        if (!adapter.isEnabled) {
            statusText.text = getString(R.string.error_bt_disabled)
            deviceAdapter.update(emptyList())
            return
        }
        val bonded = adapter.bondedDevices?.toList().orEmpty()
        if (bonded.isEmpty()) {
            statusText.text = getString(R.string.no_paired)
        } else {
            statusText.text = getString(R.string.select_device)
        }
        deviceAdapter.update(bonded)
    }

    @SuppressLint("MissingPermission")
    private fun connectTo(device: BluetoothDevice) {
        val displayName = device.name ?: device.address
        statusText.text = getString(R.string.connecting, displayName)
        Thread {
            try {
                BluetoothHelper.connect(device)
                runOnUiThread {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_DEVICE_NAME, displayName)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                BluetoothHelper.disconnect()
                runOnUiThread {
                    statusText.text =
                        getString(R.string.error_connection, e.message ?: "?")
                }
            }
        }.start()
    }
}
