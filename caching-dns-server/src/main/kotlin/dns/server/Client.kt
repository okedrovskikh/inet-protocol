package dns.server

import dns.server.DnsConstants.MAX_MESSAGE_SIZE

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class Client(private val clientSocket: DatagramSocket) {
    fun processRequest(request: ByteArray): ByteArray {
        clientSocket.connect(InetAddress.getByName("8.8.8.8"), 53)
        clientSocket.send(DatagramPacket(request, 0, request.size, InetAddress.getByName("8.8.8.8"), 53))
        val responsePacket = DatagramPacket(ByteArray(MAX_MESSAGE_SIZE), MAX_MESSAGE_SIZE)
        clientSocket.receive(responsePacket)
        clientSocket.disconnect()
        return responsePacket.data
    }
}