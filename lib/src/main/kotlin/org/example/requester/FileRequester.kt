package org.example.requester


internal interface FileRequester<ChunkId> {
    suspend fun getBodySize(): Long

    /**
        Consider the chunk is a ByteArray, we can use only int for
        addressing, so chunks should not be big.
       TODO:
        But if we are going to use some other structure than ByteArray, then
        for big chunks Stream should probably be used.
        Like in https://ktor.io/docs/client-responses.html#streaming.
     */
    suspend fun getChunk(id: ChunkId): ByteArray
}