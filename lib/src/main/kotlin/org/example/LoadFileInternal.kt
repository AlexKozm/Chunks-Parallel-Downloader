package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import org.example.requester.FileRequester
import org.example.storage.ChunksStorageScope
import org.example.storage.ChunksStorage

// TODO: think about using generic ChunkId
internal suspend fun <Result> loadFile(
    fileRequester: FileRequester<LongRange>,
    chunkSizeProvider: ChunkSizeDefiner,
    chunksStorageProvider: (bodySize: Long, chunkSize: Int) -> ChunksStorage<Result, LongRange>,
    numOfParallelRequests: Int
): Result {
    val bodySize = fileRequester.getBodySize()
    val chunkSize = chunkSizeProvider.chunkSize(bodySize)
    val chunksStorage = ChunksStorageScope(chunksStorageProvider(bodySize, chunkSize))
    val iterator = LongRangeIterator(chunkSize, bodySize)
    val semaphore = Semaphore(numOfParallelRequests)
    return chunksStorage.use {
        coroutineScope {
            for (chunkRange in iterator) {
                semaphore.acquire()
                launch {
                    val chunk = fileRequester.getChunk(chunkRange)
                    saveChunk(chunkRange, chunk)
                    semaphore.release()
                }
            }
        }
    }
}

internal class LongRangeIterator(
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