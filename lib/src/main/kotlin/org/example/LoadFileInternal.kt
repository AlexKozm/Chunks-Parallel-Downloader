package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import org.example.requester.FileRequester
import org.example.storage.ChunksStorage

// TODO: think about using generic ChunkId
internal suspend fun <Result> loadFile(
    fileRequester: FileRequester<LongRange>,
    chunkSizeProvider: (bodySize: Long) -> Int,
    chunksStorageProvider: (bodySize: Long, chunkSize: Int) -> ChunksStorage<Result, LongRange>,
    numOfParallelRequests: Int
): Result {
    val bodySize = fileRequester.getBodySize()
    val chunkSize = chunkSizeProvider(bodySize)
    val chunksStorage = chunksStorageProvider(bodySize, chunkSize)
    val iterator = LongRangeIterator(chunkSize, bodySize)
    val semaphore = Semaphore(numOfParallelRequests)
    coroutineScope {
        for (chunkRange in iterator) {
            semaphore.acquire()
            launch {
                val chunk = fileRequester.getChunk(chunkRange)
                chunksStorage.saveChunk(chunkRange, chunk)
                semaphore.release()
            }
        }
    }
    return chunksStorage.mergeChunks()
}

class LongRangeIterator(
    val chunkSize: Int,
    val bodySize: Long
) : Iterator<LongRange> {
    var chunkCounter: LongRange = -1L ..< 0L
    override fun next(): LongRange {
        val first = chunkCounter.last + 1
        val lastExclusive = (first + chunkSize).coerceAtMost(bodySize)
        chunkCounter = first ..< lastExclusive
        return chunkCounter
    }
    override fun hasNext() = chunkCounter.last != bodySize - 1
}