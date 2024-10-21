package net.msukanen.splintersector.server

import Query
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.ServerSocket
import java.net.Socket

const val SERVER_PORT = 15551

interface ServerControl {
    fun start(): Job
    fun stop()
}

/** Launches the server application.  */
object ServerLauncher : ServerControl {
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

    override fun start() = CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            serverSocket = ServerSocket(SERVER_PORT)
            serverSocket?.use {
                println("Server <START> on port $SERVER_PORT")
                while (serverSocket != null) {
                    val client = try {
                        serverSocket?.accept()
                    } catch (e: Exception) {
                        null
                    }
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
                println("""Server <SHUTDOWN> initiated.""")
            }
        }

    override fun stop() {
        serverSocket?.close()
        serverSocket = null
        println("Server <STOP> on port $SERVER_PORT.")
    }
}

private fun Socket.handleReq() {
    val r = BufferedReader(InputStreamReader(this.inputStream))
    val w = PrintWriter(this.outputStream)
    val req = r.readLine()
    val (method, path) = req?.split(" ") ?: listOf("", "")
    println("M:$method, P:$path")
    when (method) {
        "GET" -> w.handleGetRequest { path }
        else -> w.fakePaywall
    }
}

/**
 * Handle incoming GET request.
 */
private fun PrintWriter.handleGetRequest(getPath: () -> String) = when (getPath()) {
    "/splnsect/api" -> describeAPI(getPath())
    else -> {
        // Anything what is not handled, shall fall through here:
        this.sendError(HttpURLConnection.HTTP_NOT_FOUND)
        println("Unhandled client request: ${getPath()}")
    }
}

/**
 * We don't accept POST etc., yet.
 * Let's put them behind a fake "paywall" HTTP error response ;-)
 */
private val PrintWriter.fakePaywall get() = this.sendError(HttpURLConnection.HTTP_PAYMENT_REQUIRED)

/**
 * Describe the API in JSON format.
 */
private fun PrintWriter.describeAPI(part: String?) = this.send {
    val query = Query(part)
    when (query.path) {
        // root query:
        "" -> """{
            |   "call": [
            |        {"/splnsect/room": "fetch info about a specific room"},
            |        {"/splnsect/item": "fetch info about a specific item"},
            |        {"/splnsect/monster": "fetch info about a specific monster"}
            |    ]
            |}""".trimMargin()
        // describe /room usage:
        "/splnsect/api/room" -> """{
            |   "call": [
            |       {"/splnsect/room/id=x": "fetch room id `x`"}
            |   ]
            |}""".trimMargin()
        // TODO... more descriptions.
        else -> """{
            |
            |}""".trimMargin()
    }
}

/**
 * Send something, whatever the something might be.
 */
private fun PrintWriter.send(body: () -> String) = this.apply {
    val r = body()
    println("HTTP/1.1 200 OK")
    println("Content-Type: application/json")
    println("Content-Length: ${r.length}")
    println()
    println(r)
    flush()
}

/**
 * Send some error code to the client.
 */
private fun PrintWriter.sendError(code: Int) = this.apply {
    val r = """
        Page/entry not found, or not accessible, or you haven't given us money yet...
    """.trimIndent()
    println("HTTP/1.1 $code ERROR")
    println("Content-Type: text/plain")
    println("Content-Length: ${r.length}")
    println()
    println(r)
    flush()
}
