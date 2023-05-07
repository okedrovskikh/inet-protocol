package dns.server

import java.nio.ByteBuffer
import kotlin.math.abs

class Parser(data: ByteArray) {
    private val byteBuffer: ByteBuffer

    init {
        byteBuffer = ByteBuffer.wrap(data)
    }

    fun parseMessage(): Message {
        val header = parseHeader()
        return Message(
            header = header,
            questions = (0 until header.qdCount.toLong()).map { parseQuestion() },
            answers = (0 until header.anCount.toLong()).map { parseRecord() },
            authorities = (0 until header.nsCount.toLong()).map { parseRecord() },
            additions = (0 until header.arCount.toLong()).map { parseRecord() }
        )
    }

    private fun parseHeader(): Header =
        Header(
            id = byteBuffer.getUShort(),
            flags = byteBuffer.getUShort(),
            qdCount = byteBuffer.getUShort(),
            anCount = byteBuffer.getUShort(),
            nsCount = byteBuffer.getUShort(),
            arCount = byteBuffer.getUShort()
        )

    private fun parseQuestion(): Question =
        Question(
            qName = parseLabel(),
            qType = Type.getByCode(byteBuffer.short.toInt()),
            qClass = Class.getByCode(byteBuffer.short.toInt())
        )

    private fun parseRecord(): ResourceRecord {
        var rdLength: UShort
        return ResourceRecord(
            name = parseLabel(),
            type = Type.getByCode(byteBuffer.short.toInt()),
            clazz = Class.getByCode(byteBuffer.short.toInt()),
            ttl = byteBuffer.getUInt(),
            rdLength = byteBuffer.getUShort().also { rdLength = it },
            rData = parseRData(rdLength)
        )
    }

    private fun parseLabel(): String {
        val labels = mutableListOf<String>()

        var octet = byteBuffer.get().toInt()
        var position = byteBuffer.position()
        var isFirstPointer = true
        var oldPosition: Int? = null
        while (octet != 0) {

            if (octet.containsPointerBits()) {
                val nextOctet = byteBuffer.get(position).toInt().also { position += 1 }

                if (isFirstPointer) {
                    oldPosition = position
                    isFirstPointer = false
                }

                position = (octet.shl(26).shr(18) + nextOctet).shl(18).shr(18)
            } else if (octet.containsLabelBits()) {
                val data = ByteArray(octet)
                byteBuffer.get(position, data, 0, octet).also { position += octet }
                labels.add(String(data))
            } else {
                error("Not a label, not a pointer")
            }

            octet = byteBuffer.get(position).toInt().also { position += 1 }
        }
        byteBuffer.position(oldPosition ?: position)

        return labels.joinToString(separator = ".")
    }

    private fun parseRData(rdLength: UShort): ByteArray {
        val data = ByteArray(rdLength.toInt())
        byteBuffer.get(data, 0, rdLength.toInt())
        return data
    }
}

private fun Int.containsPointerBits(): Boolean =
    abs(this.shr(7)) == 1 && abs(this.shr(6)) == 1

private fun Int.containsLabelBits(): Boolean =
    this.shr(7) == 0 && this.shr(6) == 0
