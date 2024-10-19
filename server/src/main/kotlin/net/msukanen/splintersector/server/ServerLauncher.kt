package net.msukanen.splintersector.server

import java.net.ServerSocket

const val SERVER_PORT = 15551

/** Launches the server application.  */
object ServerLauncher {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = ServerSocket(SERVER_PORT)
        println("Server started on port $SERVER_PORT")
        server.use {
            while(true) {
                val client = server.accept()
                launch(Dispatchers.IO) {

                }
            }
        }
    }
}
