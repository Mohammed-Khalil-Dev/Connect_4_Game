package com.example.connect4game.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionHelper {

        fun getRequiredWifiP2pPermission(): Array<String> {
            return arrayOf(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        fun isRequiredWifiP2pPermissionGranted(context: Context): Boolean {

            return ContextCompat.checkSelfPermission(
                context, Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
        }




}