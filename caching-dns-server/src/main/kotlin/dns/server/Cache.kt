package dns.server

import java.nio.file.Path
import java.time.Duration
import java.time.Instant

class Cache : AutoCloseable {
    private val cache: MutableMap<Pair<Type, String>, MutableList<Pair<ResourceRecord, Instant>>>

    init {
        val gsonHelper = GsonHelper()
        val a = gsonHelper.loadFromJson(CACHE_PATH, HashMap::class.java)

        cache = if (a == null) {
            HashMap()
        } else {
            a as MutableMap<Pair<Type, String>, MutableList<Pair<ResourceRecord, Instant>>>
        }
    }

    fun clean() {
        val remove = mutableListOf<Pair<Pair<Type, String>, Pair<ResourceRecord, Instant>>>()

        for (e1 in cache.entries) {
            for (e2 in e1.value) {
                if (Duration.between(Instant.now(), e2.second).seconds.toUInt() > e2.second) {
                    remove.add(Pair(e1.key, e2))
                }
            }
        }

        for (e in remove) {
            cache[e.first]?.remove(e.second)
        }
    }

    fun put(resourceRecord: ResourceRecord) {
        cache.getOrPut(Pair(resourceRecord.type, resourceRecord.name)) { ArrayList() }
            .add(Pair(resourceRecord, Instant.now()))
    }

    fun get(question: Question): List<ResourceRecord> =
        cache[Pair(question.qType, question.qName)]?.map { it.first }.orEmpty()

    override fun close() {
        val gsonHelper = GsonHelper()
        gsonHelper.loadToJson(CACHE_PATH, cache)
    }

    companion object {
        private val CACHE_PATH = Path.of("${System.getProperty("user.dir")}, src, main, resources, cache.json")
    }
}