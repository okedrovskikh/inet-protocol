package dns.server

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.Instant

class Cache : AutoCloseable {
    private val cache: MutableMap<String, MutableList<Pair<ResourceRecord, Long>>>

    init {
        var data: MutableMap<String, MutableList<Pair<ResourceRecord, Long>>>? = null

        val file = CACHE_PATH.toFile()

        if (file.exists()) {
            Files.newBufferedReader(CACHE_PATH, Charset.defaultCharset()).use {
                data = Json.decodeFromString(it.readText())
            }
        }

        cache = data ?: HashMap()
    }

    fun clean() {
        val remove = mutableListOf<Pair<String, Pair<ResourceRecord, Long>>>()

        for (e1 in cache.entries) {
            for (e2 in e1.value) {
                if (Duration.between(Instant.ofEpochSecond(e2.second), Instant.now()) > Duration.ofSeconds(e2.first.ttl.toLong())) {
                    remove.add(Pair(e1.key, e2))
                }
            }
        }

        for (e in remove) {
            cache[e.first]?.remove(e.second)
        }
    }

    fun put(resourceRecord: ResourceRecord) {
        cache.getOrPut("(${resourceRecord.type} ${resourceRecord.name})") { ArrayList() }
            .add(Pair(resourceRecord, Instant.now().epochSecond))
    }

    fun get(question: Question): List<ResourceRecord> =
        cache["(${question.qType} ${question.qName})"]?.map { it.first }.orEmpty()

    override fun close() {
        Files.newBufferedWriter(CACHE_PATH, Charset.defaultCharset(), StandardOpenOption.CREATE).use {
            it.write(Json.encodeToString(cache))
        }
    }

    companion object {
        private val CACHE_PATH = Path.of(System.getProperty("user.dir"), "src", "main", "resources", "cache.json")
    }
}