package org.example

import io.ktor.client.HttpClient
import org.example.requester.HttpFileRequester
import org.example.storage.DiskChunksStorage
import org.example.utils.ceilDiv
import java.nio.file.Path


suspend fun HttpClient.loadFile(url: String, path: String, numOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = { bodySize -> bodySize ceilDiv numOfParallelRequests },
        chunksStorageProvider = { _, chunkSize ->
            DiskChunksStorage(path = Path.of(path), chunkSize = chunkSize)
        },
        numOfParallelRequests = numOfParallelRequests
    )
}

suspend fun HttpClient.loadFile(url: String, path: String, chunkSize: Int, numOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = { bodySize ->
            when {
                chunkSize * numOfParallelRequests > bodySize -> bodySize / numOfParallelRequests + 1
                chunkSize * numOfParallelRequests == bodySize -> bodySize / numOfParallelRequests
                else -> chunkSize
            }
        },
        chunksStorageProvider = { _, chunkSize ->
            DiskChunksStorage(path = Path.of(path), chunkSize = chunkSize)
        },
        numOfParallelRequests = numOfParallelRequests
    )
}