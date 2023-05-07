package dns.server

import java.nio.ByteBuffer

fun ByteBuffer.getUShort(): UShort =
    this.short.toUShort()

fun ByteBuffer.getUInt(): UInt =
    this.int.toUInt()
