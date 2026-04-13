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
    val bodyByteSize = head(url).headers["Content-Length"]?.toInt()
        ?: TODO("Throw something meaningful")
    val chunkSize = bodyByteSize / numOfParallelRequests + if (bodyByteSize % numOfParallelRequests > 0) 1 else 0
    val realNumOfParallelRequests = when (chunkSize) {
        0 -> 1
        1 -> bodyByteSize
        else -> numOfParallelRequests
    }
    val chunksStorage = RAMChunksStorage(realNumOfParallelRequests)
    coroutineScope {
        (0 ..< realNumOfParallelRequests).map { requestNumber ->
            val startOffsetInclusive = requestNumber * chunkSize
            val endOffsetExclusive =
                if (requestNumber < realNumOfParallelRequests - 1) (requestNumber + 1) * chunkSize
                else bodyByteSize

            async {
                val response = get(url) {
                    header("Range", "bytes=$startOffsetInclusive-${endOffsetExclusive - 1}")
                }
                chunksStorage.saveChunk(requestNumber, response.bodyAsBytes())
            }
        }.awaitAll()
    }
    return chunksStorage.mergeChunks()
}
