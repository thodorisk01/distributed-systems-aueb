package com.example.foodapp.network


import java.io.*
import java.net.Socket
import kotlin.concurrent.thread

class TcpClient(private val host: String, private val port: Int) {
    private lateinit var socket: Socket
    private lateinit var writer: BufferedWriter
    private lateinit var reader: BufferedReader

    fun connect() {
        thread {
            try {
                socket = Socket(host, port)
                writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun sendRequest(query: String, onResponse: (List<String>) -> Unit) {
        thread {
            try {
                writer.write(query)
                writer.newLine()
                writer.flush()

                val responses = mutableListOf<String>()
                val isBuy = query.startsWith("BUY:")

                if (isBuy) {
                    // BUY response sinlge line
                    val line = reader.readLine()
                    if (!line.isNullOrBlank() && !line.contains("Connected to")) {
                        responses.add(line)
                    }
                } else {
                    // SEARCH response single line
                    var line: String?
                    while (reader.readLine().also { line = it } != null && line != "END") {
                        if (!line.isNullOrBlank() && !line.contains("Connected to")) {
                            responses.add(line!!)
                        }
                    }
                }

                onResponse(responses)
            } catch (e: IOException) {
                e.printStackTrace()
                onResponse(listOf("❌ Σφάλμα σύνδεσης"))
            }
        }
    }

    fun close() {
        try {
            writer.close()
            reader.close()
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}