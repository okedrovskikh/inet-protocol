package dns.server

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class ParsersTest {
    @Test
    fun `test equals parsing`() {
        val expectedMessage = Message(
            header = Header(
                id = (10).toUShort(),
                flags = (490).toUShort(),
                qdCount = (1).toUShort(),
                anCount = (2).toUShort(),
                nsCount = (0).toUShort(),
                arCount = (0).toUShort()
            ),
            questions = listOf(
                Question(
                    qName = "google.com",
                    qType = Type.A,
                    qClass = Class.IN
                )
            ),
            answers = listOf(
                ResourceRecord(
                    name = "google.com",
                    type = Type.A,
                    clazz = Class.IN,
                    ttl = (500).toUInt(),
                    rdLength = (4).toUShort(),
                    rData = listOf(
                        (167).getFourthOctet(),
                        (90).getFourthOctet(),
                        (8).getFourthOctet(),
                        (1).getFourthOctet()
                    ).toByteArray()
                ),
                ResourceRecord(
                    name = "google.com",
                    type = Type.A,
                    clazz = Class.IN,
                    ttl = (500).toUInt(),
                    rdLength = (4).toUShort(),
                    rData = listOf(
                        (67).getFourthOctet(),
                        (89).getFourthOctet(),
                        (2).getFourthOctet(),
                        (4).getFourthOctet()
                    ).toByteArray()
                )
            ),
            authorities = listOf(),
            additions = listOf()
        )

        val bytesExpectedMessage = ByteParser().toBytes(expectedMessage)
        val actualMessage = Parser(bytesExpectedMessage).parseMessage()

        println(expectedMessage)
        println(actualMessage)

        Assertions.assertEquals(expectedMessage, actualMessage)
    }
}

private fun Int.getFourthOctet(): Byte = toByte()
