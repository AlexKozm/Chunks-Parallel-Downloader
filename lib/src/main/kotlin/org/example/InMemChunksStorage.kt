package org.example

internal class InMemChunksStorage(
    numOfParallelRequests: Int
) : ChunksStorage<MutableList<ByteArray>, Int> {
    private val chunksStorage: MutableList<ByteArray?> = MutableList(numOfParallelRequests) { null }

    override suspend fun saveChunk(id: Int, chunk: ByteArray) {
        chunksStorage[id] = chunk
    }

    override suspend fun mergeChunks(): MutableList<ByteArray> {
        if (chunksStorage.contains(null)) TODO("Throw something meaningful")
        @Suppress("UNCHECKED_CAST")
        return chunksStorage as MutableList<ByteArray>
    }
}