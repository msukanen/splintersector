package net.msukanen.splintersector.server

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

const val SERVER_PORT = 15551

/** Launches the server application.  */
object ServerLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        start()
    }

    private var serverSocket: ServerSocket? = null
    var running
        get() = serverSocket != null
        set(value) {
            if (value) {
                if (serverSocket == null) {
                    start()
                }
                true
            } else {
                stop()
                false
            }
        }

    fun start() = runBlocking {
        serverSocket = ServerSocket(SERVER_PORT)
        coroutineScope {
            serverSocket?.use {
                println("Server <START> on port $SERVER_PORT")
                while (serverSocket != null) {
                    val client = serverSocket?.accept()
                    launch(Dispatchers.IO) {
                        client?.use {
                            try {
                                client.handleReq()
                            } catch (e: Exception) {
                                println("Error handling request: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        serverSocket?.close()
        serverSocket = null
        println("Server <STOP> on port $SERVER_PORT.")
    }
}

private fun Socket.handleReq() {
    val r = BufferedReader(InputStreamReader(this.inputStream))
    val w = PrintWriter(this.outputStream)
    val i = mutableListOf<String>()
    val req = r.readLine()
    val (method, path) = req?.split(" ") ?: listOf("", "")
    println("M:$method, P:$path")
    if (path == "/splnsect/api" && method == "GET") {
        w.sendJson {"""{"test":"success"}"""}
    } else {
        w.sendJson {"""{"test":"failed"}""" }
        throw Exception("""handleReq() could not deal with M$method for P:$path""")
    }
}

private fun PrintWriter.sendJson(body: () -> String) = this.apply {
    val r = body()
    println("HTTP/1.1 200 OK")
    println("Content-Type: application/json")
    println("Content-Length: ${r.length}")
    println()
    println(r)
    flush()
}
