package org.example.utils

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.storage.UnsaveChunksStorage

internal class InMemChunksStorage : UnsaveChunksStorage<List<ByteArray>, LongRange> {
    private val mutex = Mutex()
    private val chunksStorage: MutableList<Pair<LongRange, ByteArray>> = mutableListOf()

    override suspend fun saveChunk(id: LongRange, chunk: ByteArray) {
        mutex.withLock {
            chunksStorage.add(id to chunk)
        }
    }

    override suspend fun open() {}

    override suspend fun mergeChunks(): List<ByteArray> {
        val sortedChunks = chunksStorage.sortedBy { it.first.first }
        sortedChunks.ranges().isContinuous()
        return sortedChunks.map { it.second }
    }

    override suspend fun close() {}

    private fun List<Pair<LongRange, ByteArray>>.ranges(): List<LongRange> = map { it.first }

    private fun List<LongRange>.isContinuous() = fold(-1L) { prevRangeLast, curRange ->
        if (prevRangeLast + 1 != curRange.first)
            throw UnionOfRangesIsNotContinuous(prevRangeLast + 1 ..< curRange.first)
        curRange.last
    }

}