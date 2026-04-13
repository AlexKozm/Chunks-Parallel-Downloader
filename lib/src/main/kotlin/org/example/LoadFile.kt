package org.example

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsBytes
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun HttpClient.loadFile(url: String, numOfParallelRequests: Int): MutableList<ByteArray> {
    val byteSize = head(url).headers["Content-Length"]?.toInt()
        ?: TODO("Throw something meaningful")

    val chunkSize = byteSize / numOfParallelRequests
    val realNumOfParallelRequests = if (chunkSize == 0) 1 else numOfParallelRequests

    val chunksStorage: MutableList<ByteArray?> = MutableList(realNumOfParallelRequests) { null }

    coroutineScope {
        (0 ..< realNumOfParallelRequests).map { requestNumber ->
            val startOffsetInclusive = requestNumber * chunkSize
            val endOffsetExclusive =
                if (requestNumber < realNumOfParallelRequests - 1) (requestNumber + 1) * chunkSize
                else byteSize

            async {
                val response = get(url) {
                    header("Range", "bytes=$startOffsetInclusive-${endOffsetExclusive - 1}")
                }
                chunksStorage[requestNumber] = response.bodyAsBytes()
            }
        }.awaitAll()
    }

    if (chunksStorage.contains(null)) TODO("Throw something meaningful")
    @Suppress("UNCHECKED_CAST")
    return chunksStorage as MutableList<ByteArray>
}
