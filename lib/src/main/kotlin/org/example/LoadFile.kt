package org.example

import io.ktor.client.HttpClient

suspend fun HttpClient.loadFile(url: String, numOfParallelRequests: Int): MutableList<ByteArray> {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = { bodySize ->
            bodySize / numOfParallelRequests + if (bodySize % numOfParallelRequests > 0) 1 else 0
        },
        chunksStorageProvider = { bodySize, chunkSize ->
            val realNumOfParallelRequests = when (chunkSize) {
                0 -> 1 // TODO: chunkSize == 0 only if bodyByteSize == 0
                1 -> bodySize
                else -> numOfParallelRequests
            }
            RAMChunksStorage(realNumOfParallelRequests)
        },
        numOfParallelRequests = numOfParallelRequests
    )
}
