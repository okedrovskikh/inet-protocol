package dns.server

data class Message(
    val header: Header,
    val questions: List<Question>,
    val answers: List<ResourceRecord>,
    val authorities: List<ResourceRecord>,
    val additions: List<ResourceRecord>
)

data class Header(
    val id: UShort,
    val flags: UShort,
    var qdCount: UShort,
    var anCount: UShort,
    var nsCount: UShort,
    var arCount: UShort
)

data class Question(
    val qName: String,
    val qType: Type,
    val qClass: Class
)

data class ResourceRecord(
    val name: String,
    val type: Type,
    val clazz: Class,
    val ttl: UInt,
    val rdLength: UShort,
    val rData: ByteArray
)

enum class Type(val code: Int) {
    A(1), NS(2), MD(3), MF(4), CNAME(5), SOA(6), MB(7), MG(8),
    MR(9), NULL(10), WKS(11), PTR(12), HINFO(13), MINFO(14), MX(15), TXT(16);

    companion object {
        @JvmStatic
        fun getByCode(code: Int): Type {
            for (type in Type.values()) {
                if (type.code == code) {
                    return type
                }
            }
            error("Unknown code $code")
        }
    }
}

enum class Class(val code: Int) {
    IN(1), CS(2), CH(3), HS(4);

    companion object {
        @JvmStatic
        fun getByCode(code: Int): Class {
            for (type in Class.values()) {
                if (type.code == code) {
                    return type
                }
            }
            error("Unknown code $code")
        }
    }
}
