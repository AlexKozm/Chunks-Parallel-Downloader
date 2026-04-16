package org.example.storage


internal interface ChunksStorage<out Result, in ChunkId> : ChunkSaver<ChunkId> {
    suspend fun open()
    suspend fun mergeChunks(): Result
    suspend fun close()
}

internal fun interface ChunkSaver<in ChunkId> {
    suspend fun saveChunk(id: ChunkId, chunk: ByteArray)
}

internal class ChunksStorageScope<out Result, in ChunkId>(
    private val chunksStorage: ChunksStorage<Result, ChunkId>
) {
    suspend fun use(block: suspend ChunkSaver<ChunkId>.() -> Unit) = with(chunksStorage) {
        try {
            open()
            block()
            mergeChunks()
        } finally {
            close()
        }
    }
}