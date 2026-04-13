package org.example

internal interface ChunksStorage<out Result, in ChunkId> {
    suspend fun saveChunk(id: ChunkId, chunk: ByteArray)
    suspend fun mergeChunks(): Result
}