package dns.server

import dns.server.DnsConstants.MAX_MESSAGE_SIZE

import java.net.DatagramPacket
import java.net.DatagramSocket

class DnsServer(
    private val serverSocket: DatagramSocket,
    private val client: Client,
    private val cache: Cache
) {
    fun processRequest() {
        cache.clean()

        val requestPacket = DatagramPacket(ByteArray(MAX_MESSAGE_SIZE), MAX_MESSAGE_SIZE)
        serverSocket.receive(requestPacket)
        val requestMessage = Parser(requestPacket.data).parseMessage()
        println("request")
        //println(requestMessage)

        val answers = mutableListOf<ResourceRecord>()

        for (question in requestMessage.questions) {
            val answer = cache.get(question)

            if (answer.isEmpty()) {
                val headServerRequest = headServerRequest(requestMessage, question)
                val headServerRequestData = ByteParser().toBytes(headServerRequest)

                val headServerResponseMessage = processRequestToHeadServer(headServerRequestData)
                println("head server")
                println(headServerResponseMessage)

                headServerResponseMessage.answers.forEach { answers.add(it) }

                updateCache(headServerResponseMessage.let { it.answers + it.authorities + it.additions })
            } else {
                answer.forEach { answers.add(it) }
            }
        }

        val responseMessage = Message(
            header = requestMessage.header.also { it.anCount = answers.size.toUShort(); it.arCount = 0u; it.nsCount = 0u },
            questions = requestMessage.questions,
            answers = answers,
            authorities = listOf(),
            additions = listOf()
        )
        println("response")
        //println(responseMessage)

        val responseData = ByteParser().toBytes(responseMessage)
        serverSocket.send(DatagramPacket(responseData, 0 , responseData.size, requestPacket.socketAddress))
    }

    private fun headServerRequest(userRequest: Message, question: Question): Message =
        Message(
            header = userRequest.header.also { it.qdCount = 1u },
            questions = listOf(question),
            answers = userRequest.answers,
            authorities = userRequest.authorities,
            additions = userRequest.additions
        )

    private fun processRequestToHeadServer(request: ByteArray): Message =
        Parser(client.processRequest(request)).parseMessage()

    private fun updateCache(answers: List<ResourceRecord>) {
        for (answer in answers) {
            cache.put(answer)
        }
    }
}