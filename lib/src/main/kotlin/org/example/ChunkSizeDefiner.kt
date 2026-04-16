package org.example

import org.example.utils.ceilDiv

internal fun interface ChunkSizeDefiner {
    companion object
    fun chunkSize(bodySize: Long): Int
}

internal fun ChunkSizeDefiner.Companion.forLoadInOneIteration(numOfParallelRequests: Int) =
    ChunkSizeDefiner { bodySize -> bodySize ceilDiv numOfParallelRequests }

internal fun ChunkSizeDefiner.Companion.byMaxChunkSizeAndMaxParallel(
    chunkSize: Int,
    numOfParallelRequests: Int
) = ChunkSizeDefiner { bodySize ->
    when {
        chunkSize.toLong() * numOfParallelRequests >= bodySize ->
            ChunkSizeDefiner.forLoadInOneIteration(numOfParallelRequests).chunkSize(bodySize)
        else -> chunkSize
    }
}

