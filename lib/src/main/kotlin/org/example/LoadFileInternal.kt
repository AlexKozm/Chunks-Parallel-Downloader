package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore

// TODO: Consider using Long instead of Int, or even some generic type for ChunkId
internal suspend fun <Result> loadFile(
    fileRequester: FileRequester<IntRange>,
    chunkSizeProvider: (bodySize: Int) -> Int,
    chunksStorageProvider: (bodySize: Int, chunkSize: Int) -> ChunksStorage<Result, Int>,
    numOfParallelRequests: Int
): Result {
    val bodySize = fileRequester.getBodySize()
    val chunkSize = chunkSizeProvider(bodySize)
    val chunksStorage = chunksStorageProvider(bodySize, chunkSize)
    val iterator = IntRangeIterator(chunkSize, bodySize)
    val semaphore = Semaphore(numOfParallelRequests)
    coroutineScope {
        for ((index, chunkRange) in iterator.withIndex()) {
            semaphore.acquire()
            launch {
                val chunk = fileRequester.getChunk(chunkRange)
                chunksStorage.saveChunk(index, chunk)
                semaphore.release()
            }
        }
    }
    return chunksStorage.mergeChunks()
}

private class IntRangeIterator(
    val chunkSize: Int,
    val bodySize: Int
) : Iterator<IntRange> {
    var chunkCounter: IntRange = -1 ..< 0
    override fun next(): IntRange {
        val first = chunkCounter.last + 1
        val lastExclusive = (first + chunkSize).coerceAtMost(bodySize)
        chunkCounter = first ..< lastExclusive
        return chunkCounter
    }
    override fun hasNext() = chunkCounter.last != bodySize - 1
}