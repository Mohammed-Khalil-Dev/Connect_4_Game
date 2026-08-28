package com.example.connect4game.network


import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.os.Bundle
import com.example.connect4game.util.PermissionHelper
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WiFiDirectManager(private val context: Context) {

    private var currentSocket: Socket? = null

    private val _incomingMovesFlow = MutableSharedFlow<Int>()
    val incomingMovesFlow: SharedFlow<Int> = _incomingMovesFlow.asSharedFlow()

    private fun Socket.inputStreamReader() = BufferedReader(InputStreamReader(inputStream))
    //auto flush: send data
    private fun Socket.outputStreamWriter() = PrintWriter(outputStream, true)

    private val _availableLobbies = MutableStateFlow<Map<String, WifiP2pDevice>>(emptyMap())
    val availableLobbies: StateFlow<Map<String, WifiP2pDevice>> = _availableLobbies.asStateFlow()

    private val _connectionInfo = MutableStateFlow<WifiP2pInfo?>(null)
    val connectionInfo: StateFlow<WifiP2pInfo?> = _connectionInfo.asStateFlow()
    companion object {
        const val GAME_PORT = "13527"
        const val GAME_INSTANCE_NAME = "Connect4_Match"
        const val GAME_SERVICE_TYPE = "_connect4._tcp"
    }

    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->
        _connectionInfo.update { info }
        CoroutineScope(Dispatchers.IO).launch {

            try {
                if (info.groupFormed && info.isGroupOwner) {
                    val serverSocket = ServerSocket(GAME_PORT.toInt())
                    val clientSocket: Socket = serverSocket.accept() // Waits for client

                    startGameDataStream(clientSocket)

                }
                else if (info.groupFormed) {
                    val hostIpAddress: String? = info.groupOwnerAddress.hostAddress
                    val hostSocket = Socket()
                    hostSocket.connect(InetSocketAddress(hostIpAddress, GAME_PORT.toInt()), 5000)
                    startGameDataStream(hostSocket)
                }
            }
            catch (e: Exception) {
                Firebase.crashlytics.recordException(e)
            }
        }
    }
    private val manager: WifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager

    // channel is the connection between the app and Android Wi-Fi Direct
    //the callbacks are delivered to the main ui thread
    private val channel: WifiP2pManager.Channel = manager.initialize( context, context.mainLooper, null)

    @SuppressLint("MissingPermission")
    fun startHostingGame() {

        if (!PermissionHelper.isRequiredWifiP2pPermissionGranted(context)) return


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
    @SuppressLint("MissingPermission")
    fun scanForGames() {
        if (!PermissionHelper.isRequiredWifiP2pPermissionGranted(context)) return

        val responseDataListener = WifiP2pManager.DnsSdServiceResponseListener { instanceName, registrationType, device ->
            if (instanceName != GAME_INSTANCE_NAME) {

                val bundleParams = Bundle().apply { putString("found_instance", instanceName) }
                Firebase.analytics.logEvent("p2p_unknown_instance_detected", bundleParams)
                return@DnsSdServiceResponseListener


            }

            val macAddress = device.deviceAddress
            connectToDevice(device)
            _availableLobbies.update { currentLobbies ->
                currentLobbies + (macAddress to device)
            }
        }

        val extraResponseDataListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomainName, recordMap, device ->
            val port = recordMap["game_port"]
            if (port == null || port != GAME_PORT) {
                val bundleParams = Bundle().apply { putString("found_port", port ?: "null") }
                Firebase.analytics.logEvent("p2p_invalid_port_detected", bundleParams)
                return@DnsSdTxtRecordListener

            }
        }
        manager.setDnsSdResponseListeners(channel, responseDataListener, extraResponseDataListener)

        val hardwareFilter = WifiP2pDnsSdServiceRequest.newInstance(GAME_SERVICE_TYPE)
        //try to prepare a request
        manager.addServiceRequest(channel, hardwareFilter, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }

            override fun onFailure(reasonCode: Int) {
                val bundleParams = Bundle().apply { putInt("reason_code", reasonCode) }
                Firebase.analytics.logEvent("p2p_addServiceRequest_failed", bundleParams)
            }
        })

        //try to start scanning (sending the request)
        manager.discoverServices(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {

            }
            override fun onFailure(reasonCode: Int) {
                val bundleParams = Bundle().apply { putInt("reason_code", reasonCode) }
                Firebase.analytics.logEvent("p2p_discoverServices_failed", bundleParams)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: WifiP2pDevice) {
        if (!PermissionHelper.isRequiredWifiP2pPermissionGranted(context)) return

        val connectionConfig = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress

            // show a popup
            wps.setup = WpsInfo.PBC
        }
        manager.connect(channel, connectionConfig, object : WifiP2pManager.ActionListener {
            override fun onFailure(reasonCode: Int) {

                val bundleParams = Bundle().apply { putInt("reason_code", reasonCode) }
                Firebase.analytics.logEvent("p2p_connect_failed", bundleParams)

            }

            override fun onSuccess() {

            }
        })


    }

    fun requestNetworkInfo() {
        manager.requestConnectionInfo(channel, connectionListener)
    }
    private fun startGameDataStream(socket: Socket) {
        if (!testConnection(socket = socket)) {
            socket.close()
            return
        }

        currentSocket = socket
        startListening(socket = currentSocket!!)

    }

    private fun startListening(socket: Socket) {
        CoroutineScope(Dispatchers.IO).launch {
            val reader = socket.inputStreamReader()
            while (isActive && socket.isConnected) {
                val incomingMessage = reader.readLine()
                if (incomingMessage != null) {
                    val dataMap = Gson().fromJson(incomingMessage, Map::class.java)
                    val playedCol = (dataMap["move"] as Double).toInt()
                    _incomingMovesFlow.emit(playedCol)


                }
            }
        }
    }

    private fun sendMyMove(socket: Socket, column: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val writer = socket.outputStreamWriter()
            val dataMap = mapOf(
                "move" to column
            )
            val jsonString = Gson().toJson(dataMap)
            writer.println(jsonString)
        }
    }

    fun onDropPiece(column: Int) {
        currentSocket?.let { socket ->
            sendMyMove(socket, column = column)
        }
    }

    private fun testConnection(socket: Socket): Boolean {

        val writer = socket.outputStreamWriter()
        val reader = socket.inputStreamReader()

        writer.println("READY")

        val response = reader.readLine()
        val isSuccessful = response == "READY"
        Firebase.analytics.logEvent("is_successful_connection", Bundle().apply {
            val localIp = (socket.localSocketAddress as? InetSocketAddress)?.address?.hostAddress ?: "Unknown"
            val remoteIp = (socket.remoteSocketAddress as? InetSocketAddress)?.address?.hostAddress ?: "Unknown"
            putBoolean("is_successful_connection", isSuccessful)
            putString("source IP", localIp)
            putString("destination IP", remoteIp)
            putInt("port", socket.port)
        })

        return isSuccessful


    }

}