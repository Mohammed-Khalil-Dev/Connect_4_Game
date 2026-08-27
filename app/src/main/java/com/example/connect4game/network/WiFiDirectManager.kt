package com.example.connect4game.network


import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import com.example.connect4game.util.PermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class WiFiDirectManager(private val context: Context) {
    private val _availableLobbies = mutableMapOf<String, WifiP2pDevice>()

    val availableLobbies: Map<String, WifiP2pDevice>
        get() = _availableLobbies.toMap()
    companion object {
        const val GAME_PORT = "13527"
        const val GAME_INSTANCE_NAME = "Connect4_Match"
        const val GAME_SERVICE_TYPE = "_connect4._tcp"
    }
    private val manager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    // channel is the connection between the app and Android Wi-Fi Direct
    //the callbacks are delivered to the main ui thread
    private val channel: WifiP2pManager.Channel = manager.initialize( context, context.mainLooper, null)

    @SuppressLint("MissingPermission")
    fun startHostingGame() {

        if (!PermissionHelper.isRequiredWifiP2pPermissionsGranted(context)) return


        val extraData = mapOf(
            "game_port" to GAME_PORT,
        )


        val broadcastResponseData = WifiP2pDnsSdServiceInfo.newInstance(
            GAME_INSTANCE_NAME,
            GAME_SERVICE_TYPE,
            extraData
        )

        // try to add a broadcast listener
        manager.addLocalService(channel, broadcastResponseData, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(reasonCode: Int) {
                val bundleParams = Bundle()
                bundleParams.putInt("reason_code", reasonCode)
                Firebase.analytics.logEvent("p2p_addLocalService_failed", bundleParams)
            }
        })
    }

    fun scanForGames() {
        if (!PermissionHelper.isRequiredWifiP2pPermissionsGranted(context)) return

        val responseDataPacketListener = WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, device ->
            if (instanceName != GAME_INSTANCE_NAME) {
                try {
                    val bundleParams = Bundle().apply { putString("found_instance", instanceName) }
                    Firebase.analytics.logEvent("p2p_unknown_instance_detected", bundleParams)
                    return@DnsSdServiceResponseListener
                }
                catch (_: Exception) {
                    return@DnsSdServiceResponseListener
                }

            }
            // unsure each entire is unique
            val macAddress = device.deviceAddress
            _availableLobbies[macAddress] = device
        }

        val extraResponseDataPacketListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, recordMap, device ->
            val port = recordMap["game_port"]
            if (port == null || port != GAME_PORT) {
                try {
                    val bundleParams = Bundle().apply { putString("found_port", port ?: "null") }
                    Firebase.analytics.logEvent("p2p_invalid_port_detected", bundleParams)
                    return@DnsSdTxtRecordListener
                }
                catch (_: Exception) {
                    return@DnsSdTxtRecordListener
                }
            }



        }
        manager.setDnsSdResponseListeners(channel, responseDataPacketListener, extraResponseDataPacketListener)

        val hardwareFilter = WifiP2pDnsSdServiceRequest.newInstance(GAME_SERVICE_TYPE)
        // try to set up a service type filter
        manager.addServiceRequest(channel, hardwareFilter, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(reasonCode: Int) {
                val bundleParams = Bundle().apply { putInt("reason_code", reasonCode) }
                Firebase.analytics.logEvent("p2p_addServiceRequest_failed", bundleParams)
            }
        })
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(device: WifiP2pDevice) {
        if (!PermissionHelper.isRequiredWifiP2pPermissionsGranted(context)) return

        val connectionConfig = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress

            // show a popup
            wps.setup = WpsInfo.PBC
        }
        manager.connect(channel, connectionConfig, object : WifiP2pManager.ActionListener {
            override fun onFailure(reasonCode: Int) {
                try {
                    val bundleParams = Bundle().apply { putInt("reason_code", reasonCode) }
                    Firebase.analytics.logEvent("p2p_connect_failed", bundleParams)
                }
                catch (_: Exception){

                }
            }

            override fun onSuccess() {

            }
        })
    }

}