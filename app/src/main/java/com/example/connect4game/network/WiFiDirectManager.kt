package com.example.connect4game.network


import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.os.Bundle
import com.example.connect4game.util.PermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import java.net.ServerSocket

class WiFiDirectManager(private val context: Context) {
    private val manager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    // channel is the connection between the app and Android Wi-Fi Direct
    //the callbacks are delivered to the main ui thread
    private val channel: WifiP2pManager.Channel = manager.initialize( context, context.mainLooper, null)

    @SuppressLint("MissingPermission")
    fun startHostingGame() {

        if (!PermissionHelper.isRequiredWifiP2pPermissionsGranted(context)) return

        val randomOpenPort: Int = ServerSocket(0).localPort
        val extraData = mapOf(
            "game_port" to randomOpenPort.toString(),
        )


        val broadcastResponseData = WifiP2pDnsSdServiceInfo.newInstance(
            "Connect4_Match",
            "_connect4._tcp",
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

}