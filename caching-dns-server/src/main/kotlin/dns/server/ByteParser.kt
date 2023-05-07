package dns.server

class ByteParser {
    fun toBytes(message: Message): ByteArray {
        val headerAsBytes = headerAsBytes(message.header)
        val questionsAsBytes = message.questions.map { questionAsBytes(it) }.flatten()
        val answersAsBytes = message.answers.map { recordAsBytes(it) }.flatten()
        val authoritiesAsBytes = message.authorities.map { recordAsBytes(it) }.flatten()
        val additionsAsBytes = message.additions.map { recordAsBytes(it) }.flatten()
        return (headerAsBytes + questionsAsBytes + answersAsBytes
                + authoritiesAsBytes + additionsAsBytes).toByteArray()
    }

    private fun headerAsBytes(header: Header): List<Byte> =
        listOf(
            header.id.toInt().getThirdOctet(),
            header.id.toInt().getFourthOctet(),
            header.flags.toInt().getThirdOctet(),
            header.flags.toInt().getFourthOctet(),
            header.qdCount.toInt().getThirdOctet(),
            header.qdCount.toInt().getFourthOctet(),
            header.anCount.toInt().getThirdOctet(),
            header.anCount.toInt().getFourthOctet(),
            header.nsCount.toInt().getThirdOctet(),
            header.nsCount.toInt().getFourthOctet(),
            header.arCount.toInt().getThirdOctet(),
            header.arCount.toInt().getFourthOctet(),
        )

    private fun questionAsBytes(question: Question): List<Byte> =
        listOf(
            labelAsBytes(question.qName),
            listOf(
                question.qType.code.getThirdOctet(),
                question.qType.code.getFourthOctet()
            ),
            listOf(
                question.qClass.code.getThirdOctet(),
                question.qClass.code.getFourthOctet()
            )
        ).flatten()

    private fun recordAsBytes(record: ResourceRecord): List<Byte> =
        listOf(
            labelAsBytes(record.name),
            listOf(
                record.type.code.getThirdOctet(),
                record.type.code.getFourthOctet()
            ),
            listOf(
                record.clazz.code.getThirdOctet(),
                record.clazz.code.getFourthOctet(),
            ),
            listOf(
                record.ttl.toLong().getFirstOctet(),
                record.ttl.toLong().getSecondOctet(),
                record.ttl.toInt().getThirdOctet(),
                record.ttl.toInt().getFourthOctet()
            ),
            listOf(
                record.rdLength.toInt().getThirdOctet(),
                record.rdLength.toInt().getFourthOctet()
            ),
            record.rData.asList()
        ).flatten()

    private fun labelAsBytes(label: String): List<Byte> =
        (label.split(".")
            .map { it.map { it.code.toByte() } }
            .map { listOf(it.size.getFourthOctet()) + it }
                + listOf(listOf((0).getFourthOctet())))
            .flatten()
}

private fun Long.getFirstOctet(): Byte = shr(24).toByte()

private fun Long.getSecondOctet(): Byte = shr(16).toByte()

private fun Int.getThirdOctet(): Byte = shr(8).toByte()

private fun Int.getFourthOctet(): Byte = toByte()
