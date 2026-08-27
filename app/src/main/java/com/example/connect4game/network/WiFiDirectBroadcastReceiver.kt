package com.example.connect4game.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

class WiFiDirectBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION  -> {


            }
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                val isEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                try {
                    val bundleParams = Bundle().apply { putBoolean("is_enabled", isEnabled) }
                    Firebase.analytics.logEvent("p2p_state_changed", bundleParams)
                }
                catch (_: Exception) {

                }
            }


        }
    }
}