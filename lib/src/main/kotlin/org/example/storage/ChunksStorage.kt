package org.example.storage

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext


internal interface UnsaveChunksStorage<out Result, in ChunkId> : ChunkSaver<ChunkId> {
    suspend fun open()
    suspend fun mergeChunks(): Result
    suspend fun close()
}

internal fun interface ChunkSaver<in ChunkId> {
    suspend fun saveChunk(id: ChunkId, chunk: ByteArray)
}

internal class ChunksStorage<out Result, in ChunkId>(
    private val chunksStorage: UnsaveChunksStorage<Result, ChunkId>
) {
    suspend fun use(block: suspend ChunkSaver<ChunkId>.() -> Unit) = with(chunksStorage) {
        try {
            open()
            block()
            mergeChunks()
        } finally {
            withContext(NonCancellable) {
                close()
            }
        }
    }
}

internal fun <Result, ChunkId> UnsaveChunksStorage<Result, ChunkId>.toSaveStorage() = ChunksStorage(this)