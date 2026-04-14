package org.example

import io.ktor.client.HttpClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun HttpClient.loadFile(url: String, numOfParallelRequests: Int): MutableList<ByteArray> {
    val fileRequester = HttpFileRequester(this, url)
    val bodyByteSize = fileRequester.getBodySize()
    val chunkSize = bodyByteSize / numOfParallelRequests + if (bodyByteSize % numOfParallelRequests > 0) 1 else 0
    val realNumOfParallelRequests = when (chunkSize) {
        0 -> 1 // TODO: chunkSize == 0 only if bodyByteSize == 0
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
                val chunk = fileRequester.getChunk(startOffsetInclusive ..< endOffsetExclusive)
                chunksStorage.saveChunk(requestNumber, chunk)
            }
        }.awaitAll()
    }
    return chunksStorage.mergeChunks()
}
