package dns.server

import dns.server.Main.WORKING

import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val clientSocket = DatagramSocket()
    val serverSocket = DatagramSocket(53, InetAddress.getLoopbackAddress())
    val cache = Cache()

    val client = Client(clientSocket)
    val server = DnsServer(serverSocket, client, cache)

    val th = thread { while (WORKING) if (readln() == "Stop") WORKING = false }

    while (WORKING) {
        server.processRequest()
    }

    th.join()

    cache.close()
    serverSocket.close()
    clientSocket.close()
}

object Main {
    var WORKING: Boolean = true
}
