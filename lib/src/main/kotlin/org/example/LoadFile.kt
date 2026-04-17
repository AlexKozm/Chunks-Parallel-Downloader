package org.example

import io.ktor.client.HttpClient
import org.example.requester.HttpFileRequester
import org.example.storage.UnsaveDiskChunksStorage
import java.nio.file.Path


suspend fun HttpClient.loadFile(url: String, path: String, numOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = ChunkSizeDefiner.forLoadInOneIteration(numOfParallelRequests),
        chunksStorageProvider = { _, _ -> UnsaveDiskChunksStorage(path = Path.of(path)) },
        numOfParallelRequests = numOfParallelRequests
    )
}

suspend fun HttpClient.loadFile(url: String, path: String, maxChunkSize: Int, maxNumOfParallelRequests: Int): Path {
    return loadFile(
        fileRequester = HttpFileRequester(this, url),
        chunkSizeProvider = ChunkSizeDefiner.byMaxChunkSizeAndMaxParallel(maxChunkSize, maxNumOfParallelRequests),
        chunksStorageProvider = { _, _ -> UnsaveDiskChunksStorage(path = Path.of(path)) },
        numOfParallelRequests = maxNumOfParallelRequests
    )
}