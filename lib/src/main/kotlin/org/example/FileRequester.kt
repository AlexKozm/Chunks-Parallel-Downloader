package org.example


internal interface FileRequester<ChunkId> {
    // TODO: int could be not enough for big files. Consider using Long
    suspend fun getBodySize(): Int

    // TODO: for big chunks Stream should probably be used.
    //       Like in https://ktor.io/docs/client-responses.html#streaming
    suspend fun getChunk(id: ChunkId): ByteArray
}