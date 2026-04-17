package org.example.storage

import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

internal fun interface ChunkSaver<ChunkId> {
    suspend fun saveChunk(id: ChunkId, chunk: ByteArray)
}

internal interface UnsaveChunksStorage<Result, ChunkId> : ChunkSaver<ChunkId> {
    suspend fun open()
    suspend fun mergeChunks(): Result
    suspend fun close()
}

internal class ChunksStorage<Result, ChunkId>(
    private val chunksStorage: UnsaveChunksStorage<Result, ChunkId>
) {
    suspend fun use(useBlock: suspend ChunkSaver<ChunkId>.() -> Unit) = with(chunksStorage) {
        try {
            open()
            useBlock()
            mergeChunks()
        } finally {
            withContext(NonCancellable) {
                close()
            }
        }
    }
}

internal fun <Result, ChunkId> UnsaveChunksStorage<Result, ChunkId>.toSaveStorage() = ChunksStorage(this)