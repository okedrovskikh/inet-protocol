package dns.server

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class GsonHelper {
    fun <T> loadFromJson(path: Path, type: java.lang.Class<T>): T? {
        val file = path.toFile()

        return if (!file.exists()) {
            null
        } else {
            file.bufferedReader(Charsets.UTF_8).use {
                return GSON.fromJson(it, type)
            }
        }
    }

    fun <T> loadToJson(path: Path, saveObject: T?) {
        saveObject?.let {
            val jsonString = GSON.toJson(saveObject)
            Files.newBufferedWriter(path, Charsets.UTF_8, StandardOpenOption.CREATE).use {
                it.write(jsonString)
                it.flush()
            }
        }
    }

    companion object {
        private val GSON: Gson = GsonBuilder()
            .setPrettyPrinting()
            .create()
    }
}