package com.edurda77.impuls.radio

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class RadioMetadataParser {
    companion object {
        private const val TAG = "RadioParser"
        private const val USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36"
        private const val ICY_METADATA_HEADER = "Icy-MetaData"
        private const val ICY_METAINT_HEADER = "icy-metaint"
        private const val METADATA_MARKER = "StreamTitle='"
    }

    /**
     * Получает текущий трек из радио-потока (ICY-протокол)
     * @param streamUrl URL радио-потока (например, https://impulsfm.ru/radio15)
     * @return Название трека или null в случае ошибки
     */
    suspend fun getCurrentTrack(streamUrl: String): String? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            // 1. Настраиваем подключение
            if (streamUrl.isNotBlank()) {
                val url = URL(streamUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", USER_AGENT)
                    setRequestProperty(ICY_METADATA_HEADER, "1") // Запрашиваем метаданные
                    connectTimeout = 10000
                    readTimeout = 10000
                }

                // 2. Получаем заголовки и находим icy-metaint
                val icyMetaInt = connection.getHeaderField(ICY_METAINT_HEADER)?.toIntOrNull()

                if (icyMetaInt == null) {
                    Log.w(TAG, "Сервер не поддерживает ICY-метаданные")
                    return@withContext null
                }

                Log.d(TAG, "ICY мета-интервал: $icyMetaInt байт")

                // 3. Читаем аудио-данные до метаданных
                inputStream = connection.inputStream
                val buffer = ByteArray(icyMetaInt + 1024) // Читаем аудио + возможные метаданные

                var totalRead = 0
                while (totalRead < icyMetaInt) {
                    val read = inputStream.read(buffer, totalRead, icyMetaInt - totalRead)
                    if (read == -1) break
                    totalRead += read
                }

                // 4. Читаем блок метаданных
                // Сначала читаем длину метаданных (1 байт * 16)
                val metadataLengthByte = inputStream.read()
                if (metadataLengthByte == -1) {
                    Log.w(TAG, "Не удалось прочитать длину метаданных")
                    return@withContext null
                }

                val metadataLength = metadataLengthByte * 16
                Log.d(TAG, "Длина метаданных: $metadataLength байт")

                if (metadataLength <= 0) {
                    return@withContext null
                }

                // Читаем сами метаданные
                val metadataBytes = ByteArray(metadataLength)
                var metadataRead = 0
                while (metadataRead < metadataLength) {
                    val read = inputStream.read(
                        metadataBytes,
                        metadataRead,
                        metadataLength - metadataRead
                    )
                    if (read == -1) break
                    metadataRead += read
                }

                // 5. Извлекаем название трека
                val metadata = String(metadataBytes, charset("UTF-8"))
                Log.d(TAG, "Сырые метаданные: ${metadata.take(200)}...")

                return@withContext extractStreamTitle(metadata)
            } else return@withContext ""

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении метаданных", e)
            return@withContext null
        } finally {
            // Закрываем соединения
            inputStream?.close()
            connection?.disconnect()
        }
    }

    /**
     * Извлекает StreamTitle из строки метаданных
     */
    private fun extractStreamTitle(metadata: String): String? {
        val startIndex = metadata.indexOf(METADATA_MARKER)
        if (startIndex == -1) return null

        val titleStart = startIndex + METADATA_MARKER.length
        val endIndex = metadata.indexOf("';", titleStart)

        if (endIndex == -1) return null

        return metadata.substring(titleStart, endIndex).trim().takeIf { it.isNotBlank() }
    }

    /**
     * Альтернативный метод - только заголовки (быстрее, но не все серверы поддерживают)
     */
    suspend fun getTrackFromHeaders(streamUrl: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL(streamUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                requestMethod = "HEAD" // Только заголовки, без загрузки потока
                setRequestProperty("User-Agent", USER_AGENT)
                setRequestProperty(ICY_METADATA_HEADER, "1")
                connectTimeout = 5000
                readTimeout = 5000
            }

            // Проверяем разные возможные заголовки
            val headers = listOf(
                "icy-name",
                "ice-name",
                "x-audiocast-name",
                "ice-description"
            )

            headers.forEach { header ->
                connection.getHeaderField(header)?.let { value ->
                    if (value.isNotBlank()) {
                        Log.d(TAG, "Найден заголовок $header: $value")
                        return@withContext value
                    }
                }
            }

            return@withContext null
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении заголовков", e)
            return@withContext null
        }
    }
}