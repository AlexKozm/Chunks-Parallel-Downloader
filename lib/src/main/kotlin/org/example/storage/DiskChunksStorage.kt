package org.example.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*


internal class DiskChunksStorage(
    val path: Path,
    val chunkSize: Int
) : ChunksStorage<Path, Int> {
    override suspend fun saveChunk(id: Int, chunk: ByteArray) {
        withContext(Dispatchers.IO) {
            FileChannel.open(path, CREATE, WRITE).use { channel ->
                channel.write(ByteBuffer.wrap(chunk), chunkSize * id.toLong())
            }
        }
    }

    override suspend fun mergeChunks(): Path {
        return path
    }
}