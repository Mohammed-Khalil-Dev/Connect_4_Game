package com.example.connect4game

import com.example.connect4game.network.WiFiDirectManager
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class LocalCommunicationTest {

    @Test
    fun testLocalSocketCommunication() = runBlocking {
        val hostIp = "127.0.0.1"
        val serverSocket = ServerSocket(WiFiDirectManager.GAME_PORT.toInt())

        // acts as other device
        val clientJob: Job = CoroutineScope(Dispatchers.IO).launch {
            val clientSocket = Socket(hostIp, WiFiDirectManager.GAME_PORT.toInt())
            val clientWriter = PrintWriter(clientSocket.outputStream, true)
            val clientReader = BufferedReader(InputStreamReader(clientSocket.inputStream))


            clientWriter.println(Gson().toJson(mapOf("move" to 3)))

            val clientReceived = clientReader.readLine()
            assertEquals("{\"move\":4}", clientReceived)

            clientSocket.close()
        }


        val acceptedSocket = serverSocket.accept()
        val serverWriter = PrintWriter(acceptedSocket.outputStream, true)
        val serverReader = BufferedReader(InputStreamReader(acceptedSocket.inputStream))


        val serverReceived = serverReader.readLine()
        assertEquals("{\"move\":3}", serverReceived)


        serverWriter.println(Gson().toJson(mapOf("move" to 4)))


        clientJob.join()

        acceptedSocket.close()
        serverSocket.close()
    }

}