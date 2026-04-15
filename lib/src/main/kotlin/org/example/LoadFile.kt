package org.example

import io.ktor.client.HttpClient
import org.example.requester.HttpFileRequester
import org.example.storage.DiskChunksStorage
import org.example.utils.ceilDiv
import org.example.utils.toIntOrThrow
import java.nio.file.Path


suspend fun HttpClient.loadFile(url: String, path: String, numOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = { bodySize -> bodySize ceilDiv numOfParallelRequests },
        chunksStorageProvider = { _, _ -> DiskChunksStorage(path = Path.of(path)) },
        numOfParallelRequests = numOfParallelRequests
    )
}

suspend fun HttpClient.loadFile(url: String, path: String, chunkSize: Int, numOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = { bodySize ->
            when {
                chunkSize.toLong() * numOfParallelRequests > bodySize ->
                    (bodySize / numOfParallelRequests + 1).toIntOrThrow()
                chunkSize.toLong() * numOfParallelRequests == bodySize ->
                    (bodySize / numOfParallelRequests).toIntOrThrow()
                else -> chunkSize
            }
        },
        chunksStorageProvider = { _, _ ->
            DiskChunksStorage(path = Path.of(path))
        },
        numOfParallelRequests = numOfParallelRequests
    )
}