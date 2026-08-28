package com.example.connect4game.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class WiFiDirectBroadcastReceiver(private val wiFiDirectManager: WiFiDirectManager) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            // group formed or destroyed
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION  -> {
                val p2pInfo = intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_INFO,
                    WifiP2pInfo::class.java
                )

                if (p2pInfo?.groupFormed == true) {
                    wiFiDirectManager.requestNetworkInfo()
                }

            }
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                val bundleParams = Bundle().apply { putBoolean("is_enabled", isEnabled) }
                Firebase.analytics.logEvent("p2p_state_changed", bundleParams)

            }


        }
    }
}