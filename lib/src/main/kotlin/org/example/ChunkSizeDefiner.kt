package org.example

import org.example.utils.ceilDiv
import org.example.utils.toIntOrThrow

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
        chunkSize.toLong() * numOfParallelRequests > bodySize ->
            (bodySize / numOfParallelRequests + 1).toIntOrThrow()
        chunkSize.toLong() * numOfParallelRequests == bodySize ->
            (bodySize / numOfParallelRequests).toIntOrThrow()
        else -> chunkSize
    }
}

