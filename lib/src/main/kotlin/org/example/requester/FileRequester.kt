package org.example.requester


internal interface FileRequester<ChunkId> {
    suspend fun getBodySize(): Long

    /**
     * Is not suitable for chunks with length > [Int.MAX_VALUE].
     * Could be improved by using streams [like here](https://ktor.io/docs/client-responses.html#streaming),
     * but probably is not needed. Just use not very big chunks.
     */
    suspend fun getChunk(id: ChunkId): ByteArray
}